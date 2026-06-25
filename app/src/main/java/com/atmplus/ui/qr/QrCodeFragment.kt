package com.atmplus.ui.qr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentQrCodeBinding
import com.atmplus.model.ScreenState
import com.atmplus.viewmodel.BankViewModel

class QrCodeFragment : Fragment() {

    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("QrCodeFragment onViewCreated")

        binding.etQrAccount.showSoftInputOnFocus = false
        com.atmplus.ui.KeyboardHelper.bindSingleEditText(
            binding.keyboardContainer.root,
            binding.etQrAccount
        )

        binding.etQrAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val account = s?.toString() ?: ""
                // basic validation for 15 digit account number
                val isValid = account.length == 15 && account.all { it.isDigit() }
                binding.btnSubmit.isEnabled = isValid
                binding.btnSubmit.alpha = if (isValid) 1.0f else 0.5f
            }
        })
        
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.alpha = 0.5f

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeFragment - Verify clicked")
            viewModel.qrAccountNumber.value = binding.etQrAccount.text.toString()
            viewModel.navigateTo(ScreenState.QR_CODE_OTP)
        }

        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeFragment - Home clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
