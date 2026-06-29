package com.atmplus.utils

object AppConstants {
    // Log Tag
    const val LOG_TAG = "ATMPlusApp"

    // Intent Extra Keys
    const val EXTRA_REQUEST_JSON = "request_json"
    const val EXTRA_RESPONSE_JSON = "response_json"
    const val EXTRA_STATUS = "status"
    const val EXTRA_FAILURE_REASON = "failure_reason"

    // Status Values
    const val STATUS_SUCCESS = "SUCCESS"
    const val STATUS_FAILED = "FAILED"

    // Failure Reasons
    const val FAILURE_REASON_INVALID_REQUEST = "INVALID_REQUEST"

    // Mock Data
    const val MOCK_CARD_NUMBER = "4111111111111111"
    const val MOCK_CARD_EXPIRY = "12/29"

    // Timeouts and Intervals
    const val OTP_TIMER_DURATION_MS = 45000L
    const val OTP_TIMER_INTERVAL_MS = 1000L
    const val AUTO_DONE_DELAY_MS = 4000L

    // Packages
    const val CHEQUE_DEPOSITOR_PACKAGE = "com.example.chequedepositor"
    const val QR_PRINTER_PACKAGE = "com.example.qrprinter"
}
