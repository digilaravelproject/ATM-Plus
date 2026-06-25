package com.atmplus.ui.qr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentQrCodeOtpBinding
import com.atmplus.model.ScreenState
import com.atmplus.viewmodel.BankViewModel

class QrCodeOtpFragment : Fragment() {

    private var _binding: FragmentQrCodeOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("QrCodeOtpFragment onViewCreated")

        val otpBoxes = listOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )

        com.atmplus.ui.KeyboardHelper.bindOtpEditTexts(
            binding.keyboardContainer.root,
            otpBoxes.toTypedArray()
        )

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val otp = otpBoxes.joinToString("") { it.text.toString() }
                val isValid = otp.length == 6
                binding.btnSubmit.isEnabled = isValid
                binding.btnSubmit.alpha = if (isValid) 1.0f else 0.5f
            }
        }

        otpBoxes.forEach { it.addTextChangedListener(textWatcher) }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.alpha = 0.5f

        viewModel.otpTimerText.observe(viewLifecycleOwner) { text ->
            binding.tvTimer.text = text
            if (text == getString(com.atmplus.R.string.resend_otp_label)) {
                binding.tvTimer.setTextColor(resources.getColor(com.atmplus.R.color.primary_blue, null))
                binding.tvTimer.setOnClickListener {
                    viewModel.startOtpTimer()
                    binding.tvTimer.setTextColor(resources.getColor(com.atmplus.R.color.grey_text, null))
                    binding.tvTimer.setOnClickListener(null)
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeOtpFragment - Confirm clicked")
            viewModel.navigateTo(ScreenState.QR_CODE_INFO)
        }

        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeOtpFragment - Home clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
