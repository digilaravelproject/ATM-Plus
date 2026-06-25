package com.atmplus.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atmplus.R
import com.atmplus.model.ScreenState
import com.atmplus.model.AadhaarData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BankViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentScreen = MutableLiveData<ScreenState>(ScreenState.WELCOME)
    val currentScreen: LiveData<ScreenState> get() = _currentScreen

    val aadhaarNumber = MutableLiveData<String>("")
    val isAadhaarValid = MutableLiveData<Boolean>(false)
    val aadhaarError = MutableLiveData<String?>(null)

    val chequeAccountNumber = MutableLiveData<String>("")
    val isChequeValid = MutableLiveData<Boolean>(false)

    val otpText = MutableLiveData<String>("")
    val isOtpValid = MutableLiveData<Boolean>(false)
    val otpError = MutableLiveData<String?>(null)

    private val _otpTimerText = MutableLiveData<String>()
    val otpTimerText: LiveData<String> get() = _otpTimerText

    private val _userProfile = MutableLiveData<com.atmplus.model.AadhaarData>()
    val userProfile: LiveData<com.atmplus.model.AadhaarData> get() = _userProfile

    private val _isApiLoading = MutableLiveData<Boolean>(false)
    val isApiLoading: LiveData<Boolean> get() = _isApiLoading

    private var countDownTimer: CountDownTimer? = null

    init {
        _otpTimerText.value = application.getString(R.string.resend_timer_prefix)
    }

    fun setAadhaar(number: String) {
        aadhaarNumber.value = number
        isAadhaarValid.value = number.length == 12 && number.all { it.isDigit() }
    }

    fun setChequeAccountNumber(number: String) {
        chequeAccountNumber.value = number
        isChequeValid.value = number.length == 15 && number.all { it.isDigit() }
    }

    fun setOtp(otp: String) {
        otpText.value = otp
        isOtpValid.value = otp.length == 6 && otp.all { it.isDigit() }
    }

    private var successTimerJob: kotlinx.coroutines.Job? = null

    fun navigateTo(screenState: ScreenState) {
        _currentScreen.value = screenState
        
        // Handle OTP Timer
        if (screenState == ScreenState.OTP) {
            startOtpTimer()
        } else {
            stopOtpTimer()
        }
        
        // Handle Success screen auto-navigation (disabled)
        cancelSuccessAutoTransition()
    }

    private fun startSuccessAutoTransition() {
        cancelSuccessAutoTransition()
        successTimerJob = viewModelScope.launch {
            delay(10000) // 10 seconds timer
            onSuccessProceed()
        }
    }

    private fun cancelSuccessAutoTransition() {
        successTimerJob?.cancel()
        successTimerJob = null
    }

    fun onAadhaarSubmit() {
        if (isAadhaarValid.value == true) {
            aadhaarError.value = null
            navigateTo(ScreenState.OTP)
        }
    }

    fun onOtpSubmit() {
        if (isOtpValid.value == true) {
            otpError.value = null
            val response = com.atmplus.utils.MockDataLoader.fetchAadhaarDetails(getApplication())
            val data = response.data
            if (response.status == "SUCCESS" && data != null) {
                val nonNullData: com.atmplus.model.AadhaarData = data
                _userProfile.value = nonNullData
                navigateTo(ScreenState.SUCCESS)
            }
        }
    }

    fun onSuccessProceed() {
        navigateTo(ScreenState.ADDITIONAL_INFO)
    }

    fun onAdditionalInfoSubmit() {
        navigateTo(ScreenState.PROCESSING)
    }

    fun onProcessingDone() {
        viewModelScope.launch {
            delay(3000) // 3 seconds delay before going home
            reset()
        }
    }

    fun startOtpTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(com.atmplus.utils.AppConstants.OTP_TIMER_DURATION_MS, com.atmplus.utils.AppConstants.OTP_TIMER_INTERVAL_MS) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val app = getApplication<Application>()
                _otpTimerText.value = String.format(app.getString(R.string.resend_timer_format), seconds)
            }

            override fun onFinish() {
                val app = getApplication<Application>()
                _otpTimerText.value = app.getString(R.string.resend_otp_label)
            }
        }.start()
    }

    fun stopOtpTimer() {
        countDownTimer?.cancel()
    }

    fun handleBackPress(): Boolean {
        val current = _currentScreen.value ?: ScreenState.WELCOME
        return when (current) {
            ScreenState.WELCOME -> false
            ScreenState.AADHAAR -> {
                reset()
                true
            }
            ScreenState.OTP -> {
                navigateTo(ScreenState.AADHAAR)
                true
            }
            ScreenState.PROCESSING -> {
                // Block all back navigation on Processing screen — user must wait
                true
            }
            ScreenState.SUCCESS -> {
                navigateTo(ScreenState.OTP)
                true
            }
            ScreenState.ADDITIONAL_INFO -> {
                navigateTo(ScreenState.SUCCESS)
                true
            }
            ScreenState.COMING_SOON -> {
                navigateTo(ScreenState.WELCOME)
                true
            }
            ScreenState.CHEQUE_DEPOSIT -> {
                reset()
                true
            }
            ScreenState.VERIFY_CHEQUE -> {
                navigateTo(ScreenState.CHEQUE_DEPOSIT)
                true
            }
            ScreenState.QR_CODE_GENERATOR -> {
                reset()
                true
            }
            ScreenState.QR_CODE_OTP -> {
                navigateTo(ScreenState.QR_CODE_GENERATOR)
                true
            }
            ScreenState.QR_CODE_INFO -> {
                navigateTo(ScreenState.QR_CODE_OTP)
                true
            }
            ScreenState.QR_CODE_PROCESSING -> {
                true // Block back navigation
            }
        }
    }

    fun reset() {
        stopOtpTimer()
        aadhaarNumber.value = ""
        isAadhaarValid.value = false
        chequeAccountNumber.value = ""
        isChequeValid.value = false
        otpText.value = ""
        isOtpValid.value = false
        otpError.value = null
        aadhaarError.value = null
        _currentScreen.value = ScreenState.WELCOME
    }

    fun clearAadhaarError() {
        aadhaarError.value = null
    }

    fun clearOtpError() {
        otpError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopOtpTimer()
    }
}
