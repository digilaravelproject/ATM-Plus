package com.atmplus.model

enum class ScreenState {
    WELCOME,
    AADHAAR,
    OTP,
    PROCESSING,
    SUCCESS,
    COMING_SOON,
    ADDITIONAL_INFO,
    CHEQUE_DEPOSIT,
    VERIFY_CHEQUE
}

data class AadhaarResponse(
    val status: String,
    val message: String,
    val data: AadhaarData?
)

data class AadhaarData(
    val name: String,
    val dob: String,
    val mobile: String,
    val address: String,
    val cityStatePin: String,
    val photoUrl: String
)
