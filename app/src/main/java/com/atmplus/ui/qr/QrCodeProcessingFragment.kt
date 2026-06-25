package com.atmplus.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentQrCodeProcessingBinding
import com.atmplus.viewmodel.BankViewModel
import com.atmplus.model.ScreenState

class QrCodeProcessingFragment : Fragment() {

    private var _binding: FragmentQrCodeProcessingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment onViewCreated")

        binding.btnDone.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - Done clicked")
            viewModel.reset()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
