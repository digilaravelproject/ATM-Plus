package com.atmplus.ui.account.otp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.R
import com.atmplus.databinding.FragmentOtpBinding
import com.atmplus.viewmodel.BankViewModel

class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()
    
    private lateinit var otpBoxes: Array<EditText>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("OtpFragment onViewCreated")

        otpBoxes = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )

        otpBoxes.forEach { it.showSoftInputOnFocus = false }

        setupOtpFocusMovement()

        // Bind custom keyboard
        com.atmplus.ui.KeyboardHelper.bindOtpEditTexts(
            binding.keyboardContainer.root,
            otpBoxes
        )

        viewModel.otpTimerText.observe(viewLifecycleOwner) { timerText ->
            binding.tvTimer.text = timerText
            if (timerText == getString(R.string.resend_otp_label)) {
                binding.tvTimer.isClickable = true
                binding.tvTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
            } else {
                binding.tvTimer.isClickable = false
                binding.tvTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text))
            }
        }

        binding.tvTimer.setOnClickListener {
            com.atmplus.utils.AppLogger.i("OtpFragment - Resend OTP clicked")
            viewModel.startOtpTimer()
        }

        viewModel.isOtpValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnSubmit.isEnabled = isValid
            binding.btnSubmit.alpha = if (isValid) 1.0f else 0.5f
        }

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("OtpFragment - Verify OTP clicked")
            viewModel.onOtpSubmit()
        }



        viewModel.otpError.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                android.widget.Toast.makeText(requireContext(), errorMessage, android.widget.Toast.LENGTH_SHORT).show()
                // Clear inputs
                otpBoxes.forEach { it.setText("") }
                otpBoxes[0].requestFocus()
                viewModel.setOtp("")
                viewModel.clearOtpError()
            }
        }


        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("OtpFragment - Previous clicked")
            viewModel.handleBackPress() // Goes back to Aadhaar screen
        }
    }

    private fun setupOtpFocusMovement() {
        for (i in otpBoxes.indices) {
            val currentBox = otpBoxes[i]

            currentBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.length == 1) {
                        if (i < otpBoxes.size - 1) {
                            otpBoxes[i + 1].requestFocus()
                        }
                    }
                    updateOtpInViewModel()
                }
            })

            currentBox.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (currentBox.text.isEmpty() && i > 0) {
                        otpBoxes[i - 1].setText("")
                        otpBoxes[i - 1].requestFocus()
                    } else {
                        currentBox.setText("")
                    }
                    updateOtpInViewModel()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun updateOtpInViewModel() {
        val otp = otpBoxes.joinToString("") { it.text.toString() }
        viewModel.setOtp(otp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
