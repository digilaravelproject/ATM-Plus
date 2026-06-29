package com.atmplus.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentQrCodeProcessingBinding
import com.atmplus.viewmodel.BankViewModel
import com.atmplus.model.ScreenState
import com.atmplus.utils.AppConstants

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

        binding.btnBack.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - Home clicked")
            viewModel.reset()
        }

        binding.btnConfirm.setOnClickListener {
            com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - Confirm clicked")
            
            // 1. Save data to file
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

            // 2. Launch external app
            if (!isPackageInstalled(AppConstants.QR_PRINTER_PACKAGE)) {
                com.atmplus.utils.AppLogger.w("QrCodeProcessingFragment - QR Printer app not installed")
                android.widget.Toast.makeText(requireContext(), "QR Printer app not installed", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = requireContext().packageManager.getLaunchIntentForPackage(AppConstants.QR_PRINTER_PACKAGE)
            if (intent != null) {
                // Sending data directly via Intent extras as well
                intent.putExtra("accountNumber", accountNumber)
                intent.putExtra("name", name)
                
                com.atmplus.utils.AppLogger.i("QrCodeProcessingFragment - Starting activity for package with data")
                startActivity(intent)
                activity?.finish()
            } else {
                com.atmplus.utils.AppLogger.e("QrCodeProcessingFragment - Launch intent returned null for ${AppConstants.QR_PRINTER_PACKAGE}")
                Toast.makeText(requireContext(), "Unable to launch QR Printer", Toast.LENGTH_SHORT).show()
            }
        }

        // Commenting out 4s timer
        // autoDoneHandler.postDelayed(autoDoneRunnable, com.atmplus.utils.AppConstants.AUTO_DONE_DELAY_MS)
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        com.atmplus.utils.AppLogger.d("isPackageInstalled - Checking package: $packageName")
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                requireContext().packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                requireContext().packageManager.getPackageInfo(packageName, 0)
            }
            com.atmplus.utils.AppLogger.d("isPackageInstalled - Package $packageName is installed")
            true
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            com.atmplus.utils.AppLogger.w("isPackageInstalled - Package $packageName is NOT installed: ${e.message}")
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoDoneHandler.removeCallbacks(autoDoneRunnable)
        _binding = null
    }
}
