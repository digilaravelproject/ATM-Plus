package com.atmplus

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.atmplus.databinding.ActivityMainBinding
import com.atmplus.model.ScreenState
import com.atmplus.ui.account.aadhaar.AadhaarFragment
import com.atmplus.ui.comingsoon.ComingSoonFragment
import com.atmplus.ui.account.otp.OtpFragment
import com.atmplus.ui.account.processing.ProcessingFragment
import com.atmplus.ui.account.success.SuccessFragment
import com.atmplus.ui.welcome.WelcomeFragment
import com.atmplus.ui.account.additional_info.AdditionalInfoFragment
import com.atmplus.ui.cheque.ChequeDepositFragment
import com.atmplus.ui.cheque.VerifyChequeFragment
import com.atmplus.utils.AppLogger
import com.atmplus.viewmodel.BankViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BankViewModel by viewModels()

    private var requestSessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.i("MainActivity onCreate - ATMPlus App started")
        
        val requestJson = intent.getStringExtra(com.atmplus.utils.AppConstants.EXTRA_REQUEST_JSON)
        AppLogger.d("Received request_json: $requestJson")
        if (requestJson != null) {
            try {
                val request = org.json.JSONObject(requestJson)
                requestSessionId = request.getString("session_id")
            } catch (e: org.json.JSONException) {
                AppLogger.e("Failed to parse request_json: ${e.message}")
                finishWithFailure(com.atmplus.utils.AppConstants.FAILURE_REASON_INVALID_REQUEST)
                return
            }
        } else {
            AppLogger.w("No request_json intent extra found. Running in standalone test mode.")
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable fullscreen / immersive mode
        enableFullscreen()

        setupNavigationObserver()
        setupBackPressedHandler()
    }

    fun onValidationComplete() {
        try {
            val userName = viewModel.userProfile.value?.name ?: ""
            val cardDetails = org.json.JSONObject().apply {
                put("name", userName)
                put("cardNumber", com.atmplus.utils.AppConstants.MOCK_CARD_NUMBER)
                put("expiryDate", com.atmplus.utils.AppConstants.MOCK_CARD_EXPIRY)
            }
            AppLogger.d("Validation Success. Card Details: $cardDetails")

            val resultIntent = android.content.Intent().apply {
                putExtra(com.atmplus.utils.AppConstants.EXTRA_STATUS, com.atmplus.utils.AppConstants.STATUS_SUCCESS)
                putExtra(com.atmplus.utils.AppConstants.EXTRA_RESPONSE_JSON, cardDetails.toString())
            }
            setResult(android.app.Activity.RESULT_OK, resultIntent)
        } catch (e: org.json.JSONException) {
            AppLogger.e("Failed to create cardDetails JSON: ${e.message}")
            setResult(android.app.Activity.RESULT_CANCELED)
        }
        finish()
    }

    fun finishWithFailure(reason: String) {
        AppLogger.w("Validation Failed. Reason: $reason")
        val resultIntent = android.content.Intent().apply {
            putExtra(com.atmplus.utils.AppConstants.EXTRA_STATUS, com.atmplus.utils.AppConstants.STATUS_FAILED)
            putExtra(com.atmplus.utils.AppConstants.EXTRA_FAILURE_REASON, reason)
        }
        setResult(android.app.Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun enableFullscreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    private fun setupNavigationObserver() {
        viewModel.currentScreen.observe(this) { screenState ->
            AppLogger.i("Navigating to Screen: $screenState")
            val fragment = when (screenState) {
                ScreenState.WELCOME -> WelcomeFragment()
                ScreenState.AADHAAR -> AadhaarFragment()
                ScreenState.OTP -> OtpFragment()
                ScreenState.PROCESSING -> ProcessingFragment()
                ScreenState.SUCCESS -> SuccessFragment()
                ScreenState.COMING_SOON -> ComingSoonFragment()
                ScreenState.ADDITIONAL_INFO -> AdditionalInfoFragment()
                ScreenState.CHEQUE_DEPOSIT -> ChequeDepositFragment()
                ScreenState.VERIFY_CHEQUE -> VerifyChequeFragment()
                ScreenState.QR_CODE_GENERATOR -> com.atmplus.ui.qr.QrCodeFragment()
                ScreenState.QR_CODE_OTP -> com.atmplus.ui.qr.QrCodeOtpFragment()
                ScreenState.QR_CODE_INFO -> com.atmplus.ui.qr.QrCodeInfoFragment()
                ScreenState.QR_CODE_PROCESSING -> com.atmplus.ui.qr.QrCodeProcessingFragment()
                null -> WelcomeFragment()
            }
            navigateToFragment(fragment)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AppLogger.i("BackPressed triggered on Screen: ${viewModel.currentScreen.value}")
                if (!viewModel.handleBackPress()) {
                    setResult(android.app.Activity.RESULT_CANCELED)
                    finish()
                }
            }
        })
    }
}