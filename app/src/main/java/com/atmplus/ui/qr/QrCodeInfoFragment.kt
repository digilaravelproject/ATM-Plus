package com.atmplus.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentQrCodeInfoBinding
import com.atmplus.model.ScreenState
import com.atmplus.viewmodel.BankViewModel

class QrCodeInfoFragment : Fragment() {

    private var _binding: FragmentQrCodeInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("QrCodeInfoFragment onViewCreated")

        binding.tvCustomerName.text = getString(com.atmplus.R.string.verify_cheque_customer_name_value)
        val maskedAccount = "XXXXXXXXXXX9876" // Mocked data
        binding.tvAccountNumber.text = maskedAccount

        binding.btnInsert.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeInfoFragment - Print QR clicked")
            viewModel.navigateTo(ScreenState.QR_CODE_PROCESSING)
        }

        binding.btnBack.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeInfoFragment - Home clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
