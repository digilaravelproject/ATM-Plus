package com.atmplus.ui.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentSuccessBinding
import com.atmplus.viewmodel.BankViewModel

class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("SuccessFragment onViewCreated")

        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            binding.tvUserName.text = profile.name
            binding.tvDob.text = profile.dob
            binding.tvMobile.text = profile.mobile
            binding.tvAddress.text = profile.address
            binding.tvCityStatePin.text = profile.cityStatePin

            val photoUrl = profile.photoUrl
            binding.ivProfilePhoto.setImageResource(com.atmplus.R.drawable.rahul_kumar) // Placeholder
            if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
                Thread {
                    try {
                        val url = java.net.URL(photoUrl)
                        val conn = url.openConnection() as java.net.HttpURLConnection
                        conn.doInput = true
                        conn.connect()
                        val bmp = android.graphics.BitmapFactory.decodeStream(conn.inputStream)
                        binding.ivProfilePhoto.post {
                            if (bmp != null) {
                                binding.ivProfilePhoto.setImageBitmap(bmp)
                            }
                        }
                    } catch (e: Exception) {
                        com.atmplus.utils.AppLogger.e("Failed to load profile image from internet: ${e.message}", e)
                    }
                }.start()
            } else {
                val context = requireContext()
                val resId = context.resources.getIdentifier(photoUrl, "drawable", context.packageName)
                if (resId != 0) {
                    binding.ivProfilePhoto.setImageResource(resId)
                }
            }
        }

        binding.btnBackSuccess.setOnClickListener {
            com.atmplus.utils.AppLogger.i("SuccessFragment - Back to Home clicked (resets state)")
            viewModel.reset()
        }

        binding.btnPreviousSuccess.setOnClickListener {
            com.atmplus.utils.AppLogger.i("SuccessFragment - Confirm clicked")
            viewModel.onSuccessProceed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
