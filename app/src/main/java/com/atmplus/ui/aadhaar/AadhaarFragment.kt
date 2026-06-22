package com.atmplus.ui.aadhaar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentAadhaarBinding
import com.atmplus.viewmodel.BankViewModel

class AadhaarFragment : Fragment() {

    private var _binding: FragmentAadhaarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAadhaarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("AadhaarFragment onViewCreated")

        // Sync text from ViewModel if already exists
        binding.etAadhaar.setText(viewModel.aadhaarNumber.value)

        // Bind custom keyboard
        binding.etAadhaar.showSoftInputOnFocus = false
        com.atmplus.ui.KeyboardHelper.bindSingleEditText(
            binding.keyboardContainer.root,
            binding.etAadhaar
        )

        binding.etAadhaar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setAadhaar(s?.toString() ?: "")
            }
        })

        viewModel.isAadhaarValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnSubmit.isEnabled = isValid
            // Visual feedback for enabled/disabled state
            binding.btnSubmit.alpha = if (isValid) 1.0f else 0.5f
        }

        viewModel.aadhaarError.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                android.widget.Toast.makeText(requireContext(), errorMessage, android.widget.Toast.LENGTH_SHORT).show()
                // Clear input
                binding.etAadhaar.setText("")
                viewModel.setAadhaar("")
                viewModel.clearAadhaarError()
            }
        }

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("AadhaarFragment - Submit clicked (Aadhaar validated)")
            viewModel.onAadhaarSubmit()
        }

        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("AadhaarFragment - Previous/Reset clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
