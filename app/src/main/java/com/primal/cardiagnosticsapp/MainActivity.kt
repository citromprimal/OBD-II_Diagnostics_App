package com.primal.cardiagnosticsapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.primal.cardiagnosticsapp.data.DynamicVehicleData
import com.primal.cardiagnosticsapp.data.StaticVehicleData
import com.primal.cardiagnosticsapp.ui.theme.CarDiagnosticsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.primal.cardiagnosticsapp.ui.theme.ThemeMode
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import java.util.Locale
import com.primal.cardiagnosticsapp.data.AuthRepository
import com.primal.cardiagnosticsapp.data.DtcRepository
import com.primal.cardiagnosticsapp.data.DtcScanHistoryRow
import com.primal.cardiagnosticsapp.data.DtcScanItemHistoryRow
import com.primal.cardiagnosticsapp.data.UserSettingsRepository
import com.primal.cardiagnosticsapp.data.VehicleRepository
import com.primal.cardiagnosticsapp.data.VehicleRow
import com.primal.cardiagnosticsapp.ui.AuthScreen
import com.primal.cardiagnosticsapp.ui.model.ConnectionStatus
import com.primal.cardiagnosticsapp.ui.model.GraphTypeKey
import com.primal.cardiagnosticsapp.ui.screens.DashboardTab
import com.primal.cardiagnosticsapp.ui.screens.DiagnosticsTab
import com.primal.cardiagnosticsapp.ui.screens.GraphsTab
import com.primal.cardiagnosticsapp.ui.screens.HistoryTab
import com.primal.cardiagnosticsapp.ui.screens.SettingsTab
import com.primal.cardiagnosticsapp.ui.screens.parseFuelType
import com.primal.cardiagnosticsapp.data.calculateFuelConsumptionLPer100
import com.primal.cardiagnosticsapp.data.calculateFuelRateLph

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = loadLanguage(newBase)

        val locale = when (lang) {
            AppLanguage.ENGLISH -> Locale("en")
            AppLanguage.SLOVAK -> Locale("sk")
            AppLanguage.SYSTEM -> Locale.getDefault()
        }

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val localizedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepo = remember { AuthRepository() }
    val settingsRepo = remember { UserSettingsRepository() }

    var isAuthed by remember { mutableStateOf(authRepo.currentUserId() != null) }

    var language by rememberSaveable { mutableStateOf(loadLanguage(context)) }
    var themeMode by rememberSaveable { mutableStateOf(loadThemeMode(context)) }

    val defaultGraphColors = remember {
        mapOf(
            GraphTypeKey.SPEED.name to 0xFF1E88E5.toInt(),
            GraphTypeKey.RPM.name to 0xFFD32F2F.toInt(),
            GraphTypeKey.COOLANT.name to 0xFFF57C00.toInt(),
            GraphTypeKey.THROTTLE.name to 0xFF8E24AA.toInt(),
            GraphTypeKey.ACCELERATOR_PEDAL.name to 0xFF00ACC1.toInt(),
            GraphTypeKey.BOOST.name to 0xFF2E7D32.toInt(),
            GraphTypeKey.MAP.name to 0xFF00838F.toInt(),
            GraphTypeKey.BARO.name to 0xFF607D8B.toInt(),
            GraphTypeKey.MAF.name to 0xFF7B1FA2.toInt(),
            GraphTypeKey.IAT.name to 0xFF3949AB.toInt(),
            GraphTypeKey.TIMING_ADVANCE.name to 0xFFC2185B.toInt(),
            GraphTypeKey.FUEL_LEVEL.name to 0xFF00897B.toInt(),
            GraphTypeKey.FUEL_CONSUMPTION.name to 0xFF6D4C41.toInt(),
            GraphTypeKey.FUEL_RATE.name to 0xFF546E7A.toInt(),
            GraphTypeKey.ENGINE_LOAD.name to 0xFF26A69A.toInt(),
            GraphTypeKey.FUEL_PRESSURE.name to 0xFFFFA000.toInt(),
            GraphTypeKey.VOLTAGE.name to 0xFFFDD835.toInt()
        )
    }

    var graphColors by rememberSaveable { mutableStateOf(defaultGraphColors) }

    LaunchedEffect(isAuthed) {
        val userId = authRepo.currentUserId()
        if (isAuthed && userId != null) {
            runCatching {
                settingsRepo.getSettings(userId)
            }.onSuccess { remote ->
                if (remote != null) {
                    val remoteTheme = runCatching { ThemeMode.valueOf(remote.theme) }
                        .getOrDefault(ThemeMode.SYSTEM)

                    val remoteLanguage = runCatching { AppLanguage.valueOf(remote.language) }
                        .getOrDefault(AppLanguage.SYSTEM)

                    themeMode = remoteTheme
                    language = remoteLanguage
                    graphColors = if (remote.graphColors.isEmpty()) defaultGraphColors
                    else defaultGraphColors + remote.graphColors

                    saveThemeMode(context, remoteTheme)
                    saveLanguage(context, remoteLanguage)
                }
            }
        }
    }

    CarDiagnosticsTheme(themeMode = themeMode) {
        if (!isAuthed) {
            AuthScreen(
                onLogin = { email, pass ->
                    authRepo.signIn(email, pass)
                    isAuthed = true
                },
                onRegister = { email, pass ->
                    authRepo.signUp(email, pass)
                    isAuthed = true
                }
            )
        } else {
            HomeScreen(
                themeMode = themeMode,
                language = language,
                graphColors = graphColors,
                onThemeChange = { newTheme ->
                    themeMode = newTheme
                    saveThemeMode(context, newTheme)

                    authRepo.currentUserId()?.let { userId ->
                        scope.launch {
                            runCatching {
                                settingsRepo.updateTheme(userId, newTheme)
                            }
                        }
                    }
                },
                onLanguageChange = { newLanguage ->
                    language = newLanguage
                    saveLanguage(context, newLanguage)

                    authRepo.currentUserId()?.let { userId ->
                        scope.launch {
                            runCatching {
                                settingsRepo.updateLanguage(userId, newLanguage)
                            }
                            (context as? Activity)?.recreate()
                        }
                    } ?: run {
                        (context as? Activity)?.recreate()
                    }
                },
                onGraphColorChange = { key, argb ->
                    graphColors = graphColors.toMutableMap().apply {
                        put(key.name, argb)
                    }

                    authRepo.currentUserId()?.let { userId ->
                        scope.launch {
                            runCatching {
                                settingsRepo.updateSingleGraphColor(userId, key.name, argb)
                            }
                        }
                    }
                },
                onLogout = {
                    scope.launch {
                        authRepo.signOut()
                        isAuthed = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    themeMode: ThemeMode,
    language: AppLanguage,
    graphColors: Map<String, Int>,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onGraphColorChange: (GraphTypeKey, Int) -> Unit,
    onLogout: () -> Unit,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepo = remember { AuthRepository() }
    val vehicleRepo = remember { VehicleRepository() }
    val dtcRepo = remember { DtcRepository() }

    val manager: ObdManager = remember {
        ClassicObdManager(context)
    }

    val diagnosticsService = remember(manager) {
        DiagnosticsService(manager)
    }

    // ─── STATE ─────────────────────────────────────────────
    var selectedTab by remember { mutableStateOf(0) }

    var status by remember { mutableStateOf(ConnectionStatus.IDLE) }
    var scanning by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    val devices by manager.devices.collectAsState()

    var staticData by remember { mutableStateOf(StaticVehicleData()) }
    var dynamicData by remember { mutableStateOf(DynamicVehicleData()) }

    var currentVehicleId by rememberSaveable { mutableStateOf<String?>(null) }

    var isReadingVin by remember { mutableStateOf(false) }
    var readingStoredDtcs by remember { mutableStateOf(false) }
    var readingPendingDtcs by remember { mutableStateOf(false) }
    var readingPermanentDtcs by remember { mutableStateOf(false) }

    var vinReadStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var dtcReadStatus by rememberSaveable { mutableStateOf<String?>(null) }
    val elmResponseBuffer = remember { StringBuilder() }

    var graphsFrozen by rememberSaveable { mutableStateOf(false) }

    val speedSeries = remember { mutableStateListOf<Float>() }
    val rpmSeries = remember { mutableStateListOf<Float>() }
    val coolantSeries = remember { mutableStateListOf<Float>() }
    val throttleSeries = remember { mutableStateListOf<Float>() }
    val acceleratorPedalSeries = remember { mutableStateListOf<Float>() }
    val boostSeries = remember { mutableStateListOf<Float>() }
    val mapSeries = remember { mutableStateListOf<Float>() }
    val baroSeries = remember { mutableStateListOf<Float>() }
    val mafSeries = remember { mutableStateListOf<Float>() }
    val iatSeries = remember { mutableStateListOf<Float>() }
    val timingAdvanceSeries = remember { mutableStateListOf<Float>() }
    val fuelLevelSeries = remember { mutableStateListOf<Float>() }
    val fuelConsumptionSeries = remember { mutableStateListOf<Float>() }
    val fuelRateSeries = remember { mutableStateListOf<Float>() }
    val engineLoadSeries = remember { mutableStateListOf<Float>() }
    val fuelPressureSeries = remember { mutableStateListOf<Float>() }
    val voltageSeries = remember { mutableStateListOf<Float>() }

    var savedVehicles by remember { mutableStateOf<List<VehicleRow>>(emptyList()) }
    var selectedVehicleScanHistory by remember { mutableStateOf<List<DtcScanHistoryRow>>(emptyList()) }
    var selectedScanId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedScanDtcItems by remember { mutableStateOf<List<DtcScanItemHistoryRow>>(emptyList()) }

    val logs = remember { mutableStateListOf<String>() }
    fun appendLog(line: String) {
        if (line.isBlank()) return
        logs += line.trim()
        if (logs.size > 80) logs.removeAt(0)
    }

    LaunchedEffect(Unit) {
        authRepo.currentUserId()?.let { userId ->
            runCatching {
                savedVehicles = vehicleRepo.getVehiclesForUser(userId)
            }.onFailure {
                appendLog("Vehicle history load failed: ${it.message}")
            }
        }
    }

    // ─── PERMISSIONS ───────────────────────────────────────
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    fun ensurePerms() {
        val needs = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 31) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) needs += Manifest.permission.BLUETOOTH_SCAN
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) needs += Manifest.permission.BLUETOOTH_CONNECT
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) needs += Manifest.permission.ACCESS_FINE_LOCATION
        }
        if (needs.isNotEmpty()) permLauncher.launch(needs.toTypedArray())
    }

    LaunchedEffect(Unit) { ensurePerms() }

    // ─── STRINGS ───────────────────────────────────────
    val tabs = listOf(
        stringResource(R.string.tab_dashboard),
        stringResource(R.string.tab_diagnostics),
        stringResource(R.string.tab_graphs),
        stringResource(R.string.tab_history),
        stringResource(R.string.tab_settings)
    )

    val vinReadingText = stringResource(R.string.reading_vin)
    val vinReceivedText = stringResource(R.string.vin_received)
    val vinNoSuppText = stringResource(R.string.vin_no_supp)
    val vinParseFailedText = stringResource(R.string.vin_parse_fail)

    val readingStoredDtcsText = stringResource(R.string.reading_stored_dtcs)
    val readingPendingDtcsText = stringResource(R.string.reading_pending_dtcs)
    val readingPermanentDtcsText = stringResource(R.string.reading_permanent_dtcs)
    val dtcsReceivedText = stringResource(R.string.dtcs_received)
    val dtcParseFailedText = stringResource(R.string.dtc_parse_failed)
    val dtcReadCompleteText = stringResource(R.string.dtc_read_complete)

    Scaffold(
    ) { inner ->

        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            when (selectedTab) {

                // ─── DASHBOARD ─────────────────────────────
                0 -> DashboardTab(
                    status = status,
                    staticData = staticData,
                    dynamicData = dynamicData
                )

                // ─── DIAGNOSTICS ───────────────────────────
                1 -> DiagnosticsTab(
                    status = status,
                    vin = staticData.vin,
                    vinInfo = staticData.vinInfo,
                    dtcs = staticData.dtcs,
                    pendingDtcs = staticData.pendingDtcs,
                    permanentDtcs = staticData.permanentDtcs,
                    canReadDtcs = isConnected && !currentVehicleId.isNullOrBlank(),
                    language = language,
                    vinStatus = vinReadStatus,
                    dtcStatus = dtcReadStatus,
                    onReadVin = {
                        scope.launch {
                            currentVehicleId = null
                            selectedVehicleScanHistory = emptyList()
                            selectedScanId = null
                            selectedScanDtcItems = emptyList()
                            staticData = staticData.copy(
                                vin = null,
                                vinInfo = null,
                                fuelType = null,
                                dtcs = emptyList(),
                                pendingDtcs = emptyList(),
                                permanentDtcs = emptyList()
                            )

                            vinReadStatus = vinReadingText
                            isReadingVin = true
                            elmResponseBuffer.clear()

                            delay(200)
                            diagnosticsService.readVin()
                        }
                    },
                    onReadDtcs = {
                        scope.launch {
                            staticData = staticData.copy(
                                dtcs = emptyList(),
                                pendingDtcs = emptyList(),
                                permanentDtcs = emptyList(),
                                lastDtcScanAt = System.currentTimeMillis()
                            )

                            isReadingVin = false
                            readingStoredDtcs = true
                            readingPendingDtcs = false
                            readingPermanentDtcs = false

                            dtcReadStatus = readingStoredDtcsText
                            elmResponseBuffer.clear()

                            delay(200)
                            diagnosticsService.readStoredDtcs()
                        }
                    },
                    onClearDtcs = {
                        scope.launch {
                            status = ConnectionStatus.CLEAR_DTC
                            manager.setPollingEnabled(false)
                            manager.send("04")
                            delay(800)

                            staticData = staticData.copy(
                                dtcs = emptyList(),
                                pendingDtcs = emptyList(),
                                permanentDtcs = emptyList(),
                                lastDtcScanAt = System.currentTimeMillis()
                            )

                            selectedScanId = null
                            selectedScanDtcItems = emptyList()

                            val userId = authRepo.currentUserId()
                            val vehicleId = currentVehicleId

                            if (userId != null && !vehicleId.isNullOrBlank()) {
                                runCatching {
                                    selectedVehicleScanHistory = dtcRepo.getScanHistory(userId, vehicleId)
                                }.onFailure {
                                    appendLog("DTC history refresh failed after clear: ${it.message}")
                                }
                            }

                            appendLog("DTCs cleared")

                            manager.setPollingEnabled(true)
                            status = ConnectionStatus.DTC_CLEARED
                        }
                    }
                )

                // ─── GRAPHS ─────────────────────────────────
                2 -> GraphsTab(
                    status = status,
                    speed = speedSeries,
                    rpm = rpmSeries,
                    coolant = coolantSeries,
                    throttle = throttleSeries,
                    acceleratorPedal = acceleratorPedalSeries,
                    boost = boostSeries,
                    map = mapSeries,
                    baro = baroSeries,
                    maf = mafSeries,
                    iat = iatSeries,
                    timingAdvance = timingAdvanceSeries,
                    fuelLevel = fuelLevelSeries,
                    fuelConsumption = fuelConsumptionSeries,
                    fuelRate = fuelRateSeries,
                    engineLoad = engineLoadSeries,
                    fuelPressure = fuelPressureSeries,
                    voltage = voltageSeries,
                    isConnected = isConnected,
                    isFrozen = graphsFrozen,
                    onFrozenChange = { graphsFrozen = it },
                    graphColors = graphColors
                )

                // ─── HISTORY ───────────────────────────────────
                3 -> HistoryTab(
                    status = status,
                    vehicles = savedVehicles,
                    activeVehicleId = currentVehicleId,
                    onDeleteVehicle = { vehicle ->
                        authRepo.currentUserId()?.let { userId ->
                            scope.launch {
                                runCatching {
                                    vehicle.id?.let { vehicleRepo.deleteVehicle(it) }

                                    savedVehicles = vehicleRepo.getVehiclesForUser(userId)

                                    if (currentVehicleId == vehicle.id) {
                                        currentVehicleId = null
                                        selectedVehicleScanHistory = emptyList()
                                        selectedScanId = null
                                        selectedScanDtcItems = emptyList()
                                        staticData = staticData.copy(
                                            vin = null,
                                            vinInfo = null,
                                            fuelType = null,
                                            dtcs = emptyList(),
                                            pendingDtcs = emptyList(),
                                            permanentDtcs = emptyList()
                                        )
                                    }
                                }.onFailure {
                                    appendLog("Vehicle delete failed: ${it.message}")
                                }
                            }
                        }
                    },
                    loadHistory = { vehicle ->
                        authRepo.currentUserId()?.let { userId ->
                            scope.launch {
                                if (!vehicle.id.isNullOrBlank()) {
                                    selectedVehicleScanHistory = dtcRepo.getScanHistory(userId, vehicle.id)
                                    selectedScanId = null
                                    selectedScanDtcItems = emptyList()
                                }
                            }
                        }
                    },
                    history = selectedVehicleScanHistory,
                    selectedScanId = selectedScanId,
                    selectedScanItems = selectedScanDtcItems,
                    onSelectScan = { scan ->
                        authRepo.currentUserId()?.let { userId ->
                            scope.launch {
                                if (selectedScanId == scan.id) {
                                    selectedScanId = null
                                    selectedScanDtcItems = emptyList()
                                } else {
                                    selectedScanId = scan.id
                                    selectedScanDtcItems = dtcRepo.getScanItems(userId, scan.id)
                                }
                            }
                        }
                    },
                    onDeleteScan = { scan ->
                        authRepo.currentUserId()?.let { userId ->
                            scope.launch {
                                runCatching {
                                    dtcRepo.deleteScan(scan.id)

                                    selectedVehicleScanHistory = dtcRepo.getScanHistory(
                                        userId = userId,
                                        vehicleId = scan.vehicleId
                                    )

                                    if (selectedScanId == scan.id) {
                                        selectedScanId = null
                                        selectedScanDtcItems = emptyList()
                                    }
                                }.onFailure {
                                    appendLog("Scan delete failed: ${it.message}")
                                }
                            }
                        }
                    }
                )

                // ─── SETTINGS ─────────────────────────────────
                4 -> SettingsTab(
                    themeMode = themeMode,
                    language = language,
                    status = status,
                    scanning = scanning,
                    devices = devices,
                    graphColors = graphColors,

                    onThemeChange = onThemeChange,
                    onLanguageChange = onLanguageChange,
                    onGraphColorChange = onGraphColorChange,

                    onScan = {
                        ensurePerms()
                        scanning = true
                        manager.startScan(
                            onError = {
                                status = ConnectionStatus.ERROR; scanning = false
                            },
                            onStarted = {
                                status = ConnectionStatus.SCANNING
                            }
                        )
                    },
                    onStop = {
                        manager.stopScan()
                        scanning = false
                        status = ConnectionStatus.SCAN_STOPPED
                    },
                    onConnect = { dev ->
                        scope.launch {
                            status = ConnectionStatus.CONNECTING
                            dynamicData = DynamicVehicleData()

                            manager.connect(
                                dev,
                                onLine = onLine@{ line ->
                                    appendLog(line)

                                    if (isReadingVin) {
                                        appendLog("VIN DEBUG RAW: $line")
                                    }

                                    elmResponseBuffer.append(line)

                                    val raw = elmResponseBuffer.toString()

                                    if (isReadingVin) {

                                        parseFuelType(raw, language)?.let { detectedFuelType ->
                                            staticData = staticData.copy(
                                                fuelType = detectedFuelType
                                            )
                                        }

                                        Elm327Parser.tryParseVin(raw)?.let { newVin ->
                                            val decoded = VinDecoder.decodeInfo(newVin)

                                            staticData = staticData.copy(
                                                vin = newVin,
                                                vinInfo = decoded
                                            )
                                            vinReadStatus = vinReceivedText

                                            authRepo.currentUserId()?.let { userId ->
                                                scope.launch {
                                                    runCatching {
                                                        val vehicle =
                                                            vehicleRepo.findOrCreateVehicle(
                                                                userId = userId,
                                                                vin = newVin,
                                                                manufacturer = decoded?.manufacturer,
                                                                country = decoded?.country,
                                                                year = decoded?.year,
                                                                wmi = decoded?.wmi,
                                                                region = decoded?.region,
                                                                checkDigitValid = decoded?.checkDigitValid,
                                                                yearCandidates = decoded?.yearCandidates,
                                                                isValidFormat = decoded?.isValidFormat
                                                                    ?: false,
                                                                isYearAmbiguous = decoded?.isYearAmbiguous
                                                                    ?: false
                                                            )

                                                        currentVehicleId = vehicle.id
                                                        savedVehicles =
                                                            vehicleRepo.getVehiclesForUser(
                                                                userId
                                                            )

                                                        val vehicleId = vehicle.id
                                                        if (!vehicleId.isNullOrBlank()) {
                                                            selectedVehicleScanHistory = dtcRepo.getScanHistory(
                                                                userId = userId,
                                                                vehicleId = vehicleId
                                                            )
                                                        }
                                                    }.onFailure {
                                                        appendLog("VIN cloud save failed: ${it.message}")
                                                        currentVehicleId = null
                                                    }
                                                }
                                            }

                                            isReadingVin = false
                                            elmResponseBuffer.clear()

                                            scope.launch {
                                                delay(300)
                                                manager.setPollingEnabled(true)
                                            }
                                        }

                                        if ("NO DATA" in raw.uppercase()) {
                                            vinReadStatus = vinNoSuppText
                                            isReadingVin = false
                                            elmResponseBuffer.clear()

                                            scope.launch {
                                                delay(300)
                                                manager.setPollingEnabled(true)
                                            }
                                        } else if ("4902" in raw.replace(" ", "")
                                                .uppercase() && staticData.vin.isNullOrBlank()
                                        ) {
                                            vinReadStatus = vinParseFailedText
                                        }

                                        return@onLine
                                    }

                                    if (readingStoredDtcs || readingPendingDtcs || readingPermanentDtcs) {
                                        val wasReadingStored = readingStoredDtcs
                                        val wasReadingPending = readingPendingDtcs
                                        val wasReadingPermanent = readingPermanentDtcs

                                        val responseMode = when {
                                            wasReadingStored -> "43"
                                            wasReadingPending -> "47"
                                            else -> "4A"
                                        }

                                        val parsedDtcs = Elm327Parser.parseDtcs(
                                            raw = raw,
                                            language = language,
                                            responseMode = responseMode
                                        )

                                        if (parsedDtcs.isNotEmpty()) {
                                            staticData = when {
                                                wasReadingStored -> staticData.copy(dtcs = parsedDtcs)
                                                wasReadingPending -> staticData.copy(pendingDtcs = parsedDtcs)
                                                else -> staticData.copy(permanentDtcs = parsedDtcs)
                                            }

                                            when {
                                                wasReadingStored -> {
                                                    dtcReadStatus = readingPendingDtcsText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = true
                                                    readingPermanentDtcs = false
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(250)
                                                        diagnosticsService.readPendingDtcs()
                                                    }
                                                }

                                                wasReadingPending -> {
                                                    dtcReadStatus = readingPermanentDtcsText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = false
                                                    readingPermanentDtcs = true
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(250)
                                                        diagnosticsService.readPermanentDtcs()
                                                    }
                                                }

                                                else -> {
                                                    dtcReadStatus = dtcsReceivedText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = false
                                                    readingPermanentDtcs = false
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(300)
                                                        diagnosticsService.resumePolling()
                                                    }
                                                }
                                            }

                                            val userId = authRepo.currentUserId()
                                            val vehicleId = currentVehicleId

                                            if (wasReadingStored && userId != null && !vehicleId.isNullOrBlank()) {
                                                scope.launch {
                                                    runCatching {
                                                        val isDuplicate =
                                                            dtcRepo.isDuplicateOfLatestScan(
                                                                userId = userId,
                                                                vehicleId = vehicleId,
                                                                dtcs = parsedDtcs
                                                            )

                                                        if (!isDuplicate) {
                                                            val scanId = dtcRepo.insertScan(
                                                                userId = userId,
                                                                vehicleId = vehicleId,
                                                                dtcs = parsedDtcs
                                                            )

                                                            dtcRepo.insertScanItems(
                                                                userId = userId,
                                                                scanId = scanId,
                                                                dtcs = parsedDtcs,
                                                                language = language
                                                            )

                                                            selectedVehicleScanHistory = dtcRepo.getScanHistory(
                                                                userId,
                                                                vehicleId
                                                            )
                                                        } else {
                                                            appendLog("Skipped duplicate DTC scan")
                                                        }
                                                    }.onFailure {
                                                        appendLog("DTC cloud save failed: ${it.message}")
                                                    }
                                                }
                                            }

                                            return@onLine
                                        }

                                        val compact = raw
                                            .replace(" ", "")
                                            .replace("\n", "")
                                            .replace("\r", "")
                                            .uppercase()

                                        val responseCompact = when {
                                            wasReadingStored -> compact.substringAfter("43", "")
                                            wasReadingPending -> compact.substringAfter("47", "")
                                            wasReadingPermanent -> compact.substringAfter("4A", "")
                                            else -> ""
                                        }

                                        val isNoDtcs =
                                            "NO DATA" in raw.uppercase() ||
                                                    responseCompact == "00" ||
                                                    responseCompact.startsWith("00")

                                        if (isNoDtcs) {
                                            staticData = when {
                                                wasReadingStored -> staticData.copy(dtcs = emptyList())
                                                wasReadingPending -> staticData.copy(pendingDtcs = emptyList())
                                                else -> staticData.copy(permanentDtcs = emptyList())
                                            }

                                            when {
                                                wasReadingStored -> {
                                                    dtcReadStatus = readingPendingDtcsText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = true
                                                    readingPermanentDtcs = false
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(250)
                                                        diagnosticsService.readPendingDtcs()
                                                    }
                                                }

                                                wasReadingPending -> {
                                                    dtcReadStatus = readingPermanentDtcsText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = false
                                                    readingPermanentDtcs = true
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(250)
                                                        diagnosticsService.readPermanentDtcs()
                                                    }
                                                }

                                                else -> {
                                                    dtcReadStatus = dtcReadCompleteText
                                                    readingStoredDtcs = false
                                                    readingPendingDtcs = false
                                                    readingPermanentDtcs = false
                                                    elmResponseBuffer.clear()

                                                    scope.launch {
                                                        delay(300)
                                                        diagnosticsService.resumePolling()
                                                    }
                                                }
                                            }

                                            return@onLine
                                        }

                                        if (
                                            (wasReadingStored && "43" in compact) ||
                                            (wasReadingPending && "47" in compact) ||
                                            (wasReadingPermanent && "4A" in compact)
                                        ) {
                                            dtcReadStatus = dtcParseFailedText
                                        }

                                        return@onLine
                                    }
                                },
                                onData = onData@{ snap ->
                                    if (!isConnected) return@onData

                                    val fuelRateLph = calculateFuelRateLph(snap.mafGps)
                                    val fuelConsumptionL100 = calculateFuelConsumptionLPer100(
                                        speedKph = snap.speedKph,
                                        mafGps = snap.mafGps
                                    )

                                    dynamicData = dynamicData.copy(
                                        rpm = snap.rpm ?: dynamicData.rpm,
                                        speedKph = snap.speedKph ?: dynamicData.speedKph,
                                        coolantC = snap.coolantC ?: dynamicData.coolantC,
                                        throttlePct = snap.throttlePct ?: dynamicData.throttlePct,
                                        acceleratorPedalPct = snap.acceleratorPedalPct
                                            ?: dynamicData.acceleratorPedalPct,
                                        boostPsi = snap.boostPsi ?: dynamicData.boostPsi,
                                        mapKpa = snap.mapKpa ?: dynamicData.mapKpa,
                                        mafGps = snap.mafGps ?: dynamicData.mafGps,
                                        iatC = snap.iatC ?: dynamicData.iatC,
                                        timingAdvanceDeg = snap.timingAdvanceDeg
                                            ?: dynamicData.timingAdvanceDeg,
                                        fuelLevelPct = snap.fuelLevelPct
                                            ?: dynamicData.fuelLevelPct,
                                        fuelRateLph = fuelRateLph ?: dynamicData.fuelRateLph,
                                        fuelConsumptionL100 = fuelConsumptionL100
                                            ?: dynamicData.fuelConsumptionL100,
                                        engineLoadPct = snap.engineLoadPct
                                            ?: dynamicData.engineLoadPct,
                                        fuelPressureKpa = snap.fuelPressureKpa
                                            ?: dynamicData.fuelPressureKpa,
                                        baroKpa = snap.baroKpa ?: dynamicData.baroKpa,
                                        controlModuleVoltage = snap.controlModuleVoltage
                                            ?: dynamicData.controlModuleVoltage
                                    )

                                    if (!graphsFrozen) {
                                        snap.speedKph?.let {
                                            speedSeries.add(it.toFloat())
                                            if (speedSeries.size > 60) speedSeries.removeAt(0)
                                        }

                                        snap.rpm?.let {
                                            rpmSeries.add(it.toFloat())
                                            if (rpmSeries.size > 60) rpmSeries.removeAt(0)
                                        }

                                        snap.coolantC?.let {
                                            coolantSeries.add(it.toFloat())
                                            if (coolantSeries.size > 60) coolantSeries.removeAt(0)
                                        }

                                        snap.throttlePct?.let {
                                            throttleSeries.add(it.toFloat())
                                            if (throttleSeries.size > 60) throttleSeries.removeAt(0)
                                        }

                                        snap.acceleratorPedalPct?.let {
                                            acceleratorPedalSeries.add(it)
                                            if (acceleratorPedalSeries.size > 60) acceleratorPedalSeries.removeAt(
                                                0
                                            )
                                        }

                                        snap.boostPsi?.let {
                                            boostSeries.add(it.toFloat())
                                            if (boostSeries.size > 60) boostSeries.removeAt(0)
                                        }

                                        snap.mapKpa?.let {
                                            mapSeries.add(it.toFloat())
                                            if (mapSeries.size > 60) mapSeries.removeAt(0)
                                        }

                                        snap.baroKpa?.let {
                                            baroSeries.add(it.toFloat())
                                            if (baroSeries.size > 60) baroSeries.removeAt(0)
                                        }

                                        snap.mafGps?.let {
                                            mafSeries.add(it)
                                            if (mafSeries.size > 60) mafSeries.removeAt(0)
                                        }

                                        snap.iatC?.let {
                                            iatSeries.add(it.toFloat())
                                            if (iatSeries.size > 60) iatSeries.removeAt(0)
                                        }

                                        snap.timingAdvanceDeg?.let {
                                            timingAdvanceSeries.add(it)
                                            if (timingAdvanceSeries.size > 60) timingAdvanceSeries.removeAt(
                                                0
                                            )
                                        }

                                        snap.fuelLevelPct?.let {
                                            fuelLevelSeries.add(it)
                                            if (fuelLevelSeries.size > 60) fuelLevelSeries.removeAt(
                                                0
                                            )
                                        }

                                        fuelConsumptionL100?.let {
                                            fuelConsumptionSeries.add(it)
                                            if (fuelConsumptionSeries.size > 60) fuelConsumptionSeries.removeAt(
                                                0
                                            )
                                        }

                                        fuelRateLph?.let {
                                            fuelRateSeries.add(it)
                                            if (fuelRateSeries.size > 60) fuelRateSeries.removeAt(0)
                                        }

                                        snap.engineLoadPct?.let {
                                            engineLoadSeries.add(it)
                                            if (engineLoadSeries.size > 60) engineLoadSeries.removeAt(
                                                0
                                            )
                                        }

                                        snap.fuelPressureKpa?.let {
                                            fuelPressureSeries.add(it.toFloat())
                                            if (fuelPressureSeries.size > 60) fuelPressureSeries.removeAt(
                                                0
                                            )
                                        }

                                        snap.controlModuleVoltage?.let {
                                            voltageSeries.add(it)
                                            if (voltageSeries.size > 60) voltageSeries.removeAt(0)
                                        }
                                    }
                                },
                                onStatus = { msg ->
                                    appendLog(msg)

                                    val normalized = msg.trim().lowercase()

                                    when {
                                        "gatt error" in normalized ||
                                                "failed" in normalized ||
                                                "uart service not found" in normalized ||
                                                "missing bluetooth" in normalized ||
                                                "error" in normalized -> {
                                            status = ConnectionStatus.ERROR
                                            isConnected = false
                                            dynamicData = DynamicVehicleData()
                                            graphsFrozen = false
                                        }

                                        "disconnected" in normalized -> {
                                            status = ConnectionStatus.DISCONNECTED
                                            isConnected = false
                                            dynamicData = DynamicVehicleData()
                                            graphsFrozen = false
                                        }

                                        normalized.startsWith("connected") ||
                                                "connected (mock)" in normalized ||
                                                "polling:" in normalized -> {
                                            status = ConnectionStatus.CONNECTED
                                            isConnected = true
                                        }

                                        "discovering services" in normalized ||
                                                normalized.startsWith("→") -> {
                                            status = ConnectionStatus.CONNECTING
                                            isConnected = false
                                        }

                                        "initializing elm327" in normalized -> {
                                            if (!isConnected) {
                                                status = ConnectionStatus.CONNECTING
                                            }
                                        }

                                        else -> {
                                            // leave current status unchanged
                                        }
                                    }
                                }
                            )
                        }
                    },
                    onDisconnect = {
                        scope.launch {
                            manager.disconnect()
                            status = ConnectionStatus.DISCONNECTED
                            isConnected = false
                        }
                    },
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun StatusText(status: ConnectionStatus) {
    val text = when (status) {
        ConnectionStatus.IDLE -> stringResource(R.string.idle)
        ConnectionStatus.SCANNING -> stringResource(R.string.scanning)
        ConnectionStatus.SCAN_STOPPED -> stringResource(R.string.scan_stop)
        ConnectionStatus.CONNECTING -> stringResource(R.string.connecting)
        ConnectionStatus.CONNECTED -> stringResource(R.string.connected)
        ConnectionStatus.DISCONNECTED -> stringResource(R.string.disconnected)
        ConnectionStatus.ERROR -> stringResource(R.string.error)
        ConnectionStatus.CLEAR_DTC -> stringResource(R.string.clear_dtc)
        ConnectionStatus.DTC_CLEARED -> stringResource(R.string.dtc_cleared)
    }

    Text(text)
}
