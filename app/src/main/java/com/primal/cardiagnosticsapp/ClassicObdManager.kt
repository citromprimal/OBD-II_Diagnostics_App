package com.primal.cardiagnosticsapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.UUID

class ClassicObdManager(private val context: Context) : ObdManager {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _devices = MutableStateFlow<List<DeviceItem>>(emptyList())
    override val devices: StateFlow<List<DeviceItem>> = _devices.asStateFlow()

    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    private var readerJob: Job? = null
    private var pollJob: Job? = null
    private var writerJob: Job? = null

    private val buffer = StringBuilder()
    private val cmdChannel = Channel<String>(capacity = 32)

    @Volatile
    private var polling = true

    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val pollPids = mutableListOf("04", "0A", "0C", "0D", "05", "0B", "0E", "0F", "10", "11", "2F", "33", "42", "49", "4A", "4B")

    private fun hasBtConnectPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= 31)
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        else true

    private fun hasBtScanPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= 31)
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        else true

    @SuppressLint("MissingPermission")
    override fun startScan(onError: (String) -> Unit, onStarted: () -> Unit) {
        if (!hasBtScanPermission() || !hasBtConnectPermission()) {
            onError("Missing Bluetooth permission")
            return
        }

        @Suppress("DEPRECATION")
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !adapter.isEnabled) {
            onError("Bluetooth disabled")
            return
        }

        val paired = adapter.bondedDevices
            ?.map { DeviceItem(it.name ?: "(no name)", it.address) }
            ?.sortedBy { it.name.lowercase() }
            .orEmpty()

        _devices.value = paired
        onStarted()
    }

    override fun stopScan() {
        // no-op for paired classic devices list
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(
        device: DeviceItem,
        onLine: (String) -> Unit,
        onData: (VehicleSnapshot) -> Unit,
        onStatus: (String) -> Unit
    ) {
        if (!hasBtConnectPermission()) {
            onStatus("Missing Bluetooth connect permission")
            return
        }

        stopScan()

        withContext(Dispatchers.IO) {
            disconnectInternal()

            @Suppress("DEPRECATION")
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter == null || !adapter.isEnabled) {
                onStatus("Bluetooth disabled")
                return@withContext
            }

            val real: BluetoothDevice = adapter.getRemoteDevice(device.address)

            onStatus("Connecting (classic Bluetooth)...")
            adapter.cancelDiscovery()

            val createdSocket = try {
                real.createRfcommSocketToServiceRecord(sppUuid)
            } catch (e: Exception) {
                onStatus("Classic socket creation failed: ${e.message}")
                return@withContext
            }

            try {
                createdSocket.connect()
                socket = createdSocket
                input = createdSocket.inputStream
                output = createdSocket.outputStream
            } catch (e: Exception) {
                try {
                    createdSocket.close()
                } catch (_: Exception) {
                }
                onStatus("Classic Bluetooth connect failed: ${e.message}")
                return@withContext
            }

            writerJob?.cancel()
            writerJob = scope.launch { writerLoop() }

            readerJob?.cancel()
            readerJob = scope.launch {
                readLoop(onLine, onData, onStatus)
            }

            onStatus("Connected; initializing ELM327...")
            scope.launch { initSequence(onStatus) }
        }
    }

    override suspend fun send(cmd: String) {
        cmdChannel.send(cmd)
    }

    override suspend fun setPollingEnabled(enabled: Boolean) {
        polling = enabled
    }

    override suspend fun disconnect() {
        disconnectInternal()
    }

    private suspend fun writerLoop() {
        for (cmd in cmdChannel) {
            writeAscii(cmd)
            delay(250)
        }
    }

    private fun readLoop(
        onLine: (String) -> Unit,
        onData: (VehicleSnapshot) -> Unit,
        onStatus: (String) -> Unit
    ) {
        val localInput = input ?: return
        val temp = ByteArray(1024)

        try {
            while (scope.isActive) {
                val count = localInput.read(temp)
                if (count <= 0) continue

                val s = String(temp, 0, count, Charset.forName("UTF-8"))
                synchronized(buffer) { buffer.append(s) }
                onLine(s)

                val text = synchronized(buffer) { buffer.toString() }
                if (text.contains(">")) {
                    val frame = text.substringBeforeLast(">")
                    synchronized(buffer) { buffer.clear() }

                    val sup = Elm327Parser.supportedPidSetFrom4100(frame)
                    if (sup.isNotEmpty()) {
                        pollPids.clear()
                        listOf(
                            "04", // Engine load
                            "0A", // Fuel pressure
                            "0C", // RPM
                            "0D", // Speed
                            "05", // Coolant
                            "0B", // MAP
                            "0E", // Timing advance
                            "0F", // IAT
                            "10", // MAF
                            "11", // Throttle
                            "2F", // Fuel level
                            "33", // BARO
                            "42", // Control module voltage
                            "49", // Accelerator pedal D
                            "4A", // Accelerator pedal E
                            "4B"  // Accelerator pedal F
                        ).filterTo(pollPids) { it in sup }

                        onStatus("Polling: ${pollPids.joinToString(",")}")
                    }

                    Elm327Parser.parseFrame(frame)?.let { snap ->
                        onData(snap)
                    }
                }
            }
        } catch (e: Exception) {
            onStatus("Disconnected")
        }
    }

    private fun writeAscii(s: String) {
        val out = output ?: return
        out.write("$s\r".toByteArray(Charsets.UTF_8))
        out.flush()
    }

    private suspend fun initSequence(onStatus: (String) -> Unit) {
        val cmds = listOf("ATZ", "ATE0", "ATL0", "ATS0", "ATH0", "ATSP0", "0100")
        for (c in cmds) {
            onStatus("→ $c")
            writeAscii(c)
            delay(300)
        }

        onStatus("→ 0100")
        writeAscii("0100")
        delay(400)

        pollJob?.cancel()
        pollJob = scope.launch {
            while (socket?.isConnected == true) {
                val cycle = if (pollPids.isNotEmpty()) {
                    val dynamic = pollPids.map { "01$it" }.toMutableList()
                    if ("0133" !in dynamic) dynamic += "0133"
                    if ("0142" !in dynamic) dynamic += "0142"
                    dynamic
                } else {
                    listOf(
                        "0104", // Engine load
                        "010A", // Fuel pressure
                        "010C", // RPM
                        "010D", // Speed
                        "0105", // Coolant
                        "010B", // MAP
                        "010E", // Timing advance
                        "010F", // IAT
                        "0110", // MAF
                        "0111", // Throttle position
                        "0149", // Accelerator pedal position D
                        "014A", // Accelerator pedal position E
                        "014B", // Accelerator pedal position F
                        "012F", // Fuel level
                        "0133", // BARO
                        "0142"  // Control module voltage
                    )
                }

                for (cmd in cycle) {
                    if (socket?.isConnected != true) break
                    if (!polling) {
                        delay(150)
                        continue
                    }
                    writeAscii(cmd)
                    delay(250)
                }
            }
        }
    }

    private fun disconnectInternal() {
        readerJob?.cancel()
        readerJob = null

        writerJob?.cancel()
        writerJob = null

        pollJob?.cancel()
        pollJob = null

        input = null
        output = null

        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null

        scope.coroutineContext.cancelChildren()
    }
}