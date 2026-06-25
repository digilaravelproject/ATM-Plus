package com.atmplus.ui.cheque

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentChequeDepositBinding
import com.atmplus.model.ScreenState
import com.atmplus.viewmodel.BankViewModel

class ChequeDepositFragment : Fragment() {

    private var _binding: FragmentChequeDepositBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChequeDepositBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("ChequeDepositFragment onViewCreated")

        // Sync text from ViewModel if already exists
        binding.etChequeAccount.setText(viewModel.chequeAccountNumber.value)

        // Bind custom keyboard
        binding.etChequeAccount.showSoftInputOnFocus = false
        com.atmplus.ui.KeyboardHelper.bindSingleEditText(
            binding.keyboardContainer.root,
            binding.etChequeAccount
        )

        binding.etChequeAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setChequeAccountNumber(s?.toString() ?: "")
            }
        })

        viewModel.isChequeValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnSubmit.isEnabled = isValid
            // Visual feedback for enabled/disabled state
            binding.btnSubmit.alpha = if (isValid) 1.0f else 0.5f
        }

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("ChequeDepositFragment - Submit/Continue clicked")
            viewModel.navigateTo(ScreenState.VERIFY_CHEQUE)
        }

        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("ChequeDepositFragment - Home clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
