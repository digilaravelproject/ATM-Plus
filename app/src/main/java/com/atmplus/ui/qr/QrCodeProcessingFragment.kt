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

    private val autoDoneHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val autoDoneRunnable = Runnable {
        com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - 4s Timer finished. Triggering auto-done.")

        val name = viewModel.userProfile.value?.name ?: "Unknown"
        val accountNumber = viewModel.qrAccountNumber.value ?: "Unknown"
        val content = "$name\n$accountNumber"

        try {
            val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = java.io.File(dir, "CardAccountNumber.txt")
            file.writeText(content)
            com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - CardAccountNumber.txt written with data: $content")
        } catch (e: Exception) {
            com.atmplus.utils.AppLogger.e("QrCodeProcessingFragment - Failed to write CardAccountNumber.txt: ${e.message}")
        }

        (activity as? com.atmplus.MainActivity)?.onQrValidationComplete()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment onViewCreated - Starting 4s auto-done timer")

        binding.btnDone.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - Done clicked")
            (activity as? com.atmplus.MainActivity)?.onQrValidationComplete()
        }

        autoDoneHandler.postDelayed(autoDoneRunnable, com.atmplus.utils.AppConstants.AUTO_DONE_DELAY_MS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoDoneHandler.removeCallbacks(autoDoneRunnable)
        _binding = null
    }
}
