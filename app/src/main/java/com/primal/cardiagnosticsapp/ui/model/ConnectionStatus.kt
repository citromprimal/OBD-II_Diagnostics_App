package com.primal.cardiagnosticsapp.ui.model


enum class ConnectionStatus {
    IDLE,
    SCANNING,
    SCAN_STOPPED,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    CLEAR_DTC,
    DTC_CLEARED,
    ERROR
}
