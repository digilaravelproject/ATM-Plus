package com.atmplus.ui.additional_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentAdditionalInfoBinding
import com.atmplus.viewmodel.BankViewModel

class AdditionalInfoFragment : Fragment() {

    private var _binding: FragmentAdditionalInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("AdditionalInfoFragment onViewCreated")

        // Select defaults
        binding.toggleGroupGender.check(binding.btnGenderMale.id)
        binding.toggleGroupPassbook.check(binding.btnPassbookNo.id)
        binding.toggleGroupDebit.check(binding.btnDebitNo.id)
        binding.toggleGroupInternet.check(binding.btnInternetNo.id)
        binding.toggleGroupMobile.check(binding.btnMobileNo.id)

        binding.btnSubmit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("AdditionalInfoFragment - Submit clicked")
            viewModel.onAdditionalInfoSubmit()
        }

        binding.btnPrevious.setOnClickListener {
            com.atmplus.utils.AppLogger.i("AdditionalInfoFragment - Previous clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
