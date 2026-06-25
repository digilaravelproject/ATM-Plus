package com.atmplus.ui.cheque

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentVerifyChequeBinding
import com.atmplus.utils.AppConstants
import com.atmplus.viewmodel.BankViewModel

class VerifyChequeFragment : Fragment() {

    private var _binding: FragmentVerifyChequeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyChequeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("VerifyChequeFragment onViewCreated")

        // Mask and show account number entered in step 1
        val rawAccount = viewModel.chequeAccountNumber.value ?: ""
        val maskedAccount = if (rawAccount.length >= 4) {
            "X".repeat(rawAccount.length - 4) + rawAccount.takeLast(4)
        } else {
            rawAccount
        }
        binding.tvAccountNumber.text = maskedAccount

        binding.btnInsert.setOnClickListener {
            com.atmplus.utils.AppLogger.i("VerifyChequeFragment - Insert clicked")
            launchChequeDepositor()
        }

        binding.btnBack.setOnClickListener {
            com.atmplus.utils.AppLogger.i("VerifyChequeFragment - Home clicked")
            viewModel.reset()
        }
    }

    private fun launchChequeDepositor() {
        com.atmplus.utils.AppLogger.i("launchChequeDepositor - Attempting to launch Cheque Depositor app")
        if (!isPackageInstalled(AppConstants.CHEQUE_DEPOSITOR_PACKAGE)) {
            com.atmplus.utils.AppLogger.w("launchChequeDepositor - Cheque Depositor app not installed")
            Toast.makeText(requireContext(), "Cheque Depositor app not installed", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = requireContext().packageManager.getLaunchIntentForPackage(AppConstants.CHEQUE_DEPOSITOR_PACKAGE)
        if (intent != null) {
            com.atmplus.utils.AppLogger.i("launchChequeDepositor - Starting activity for package ${AppConstants.CHEQUE_DEPOSITOR_PACKAGE}")
            startActivity(intent)
            activity?.finish()
        } else {
            com.atmplus.utils.AppLogger.e("launchChequeDepositor - Launch intent returned null for ${AppConstants.CHEQUE_DEPOSITOR_PACKAGE}")
            Toast.makeText(requireContext(), "Unable to launch Cheque Depositor", Toast.LENGTH_SHORT).show()
        }
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
        _binding = null
    }
}
