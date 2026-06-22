package com.atmplus.ui.processing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentProcessingBinding
import com.atmplus.viewmodel.BankViewModel

class ProcessingFragment : Fragment() {

    private var _binding: FragmentProcessingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    private val autoDoneHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val autoDoneRunnable = Runnable {
        com.atmplus.utils.AppLogger.i("ProcessingFragment - 4s Timer finished. Triggering auto-done.")

        val name = viewModel.userProfile.value?.name ?: "Unknown"
        try {
            val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = java.io.File(dir, "Cardname.txt")
            file.writeText(name)
            com.atmplus.utils.AppLogger.i("ProcessingFragment - Cardname.txt written with name: $name")
        } catch (e: Exception) {
            com.atmplus.utils.AppLogger.e("ProcessingFragment - Failed to write Cardname.txt: ${e.message}")
        }

        (activity as? com.atmplus.MainActivity)?.onValidationComplete()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("ProcessingFragment onViewCreated - Starting 4s auto-done timer")

        binding.btnDone.setOnClickListener {
            com.atmplus.utils.AppLogger.i("ProcessingFragment - Done clicked")
            (activity as? com.atmplus.MainActivity)?.onValidationComplete()
        }

        autoDoneHandler.postDelayed(autoDoneRunnable, com.atmplus.utils.AppConstants.AUTO_DONE_DELAY_MS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoDoneHandler.removeCallbacks(autoDoneRunnable)
        _binding = null
    }
}
