package com.atmplus.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.atmplus.databinding.FragmentWelcomeBinding
import com.atmplus.model.ScreenState
import com.atmplus.viewmodel.BankViewModel

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.atmplus.utils.AppLogger.i("WelcomeFragment onViewCreated")

        binding.root.findViewById<View>(com.atmplus.R.id.powered_by_layout)?.visibility = View.VISIBLE

        binding.cardAccountOpening.setOnClickListener {
            com.atmplus.utils.AppLogger.i("WelcomeFragment - Account Opening clicked")
            viewModel.navigateTo(ScreenState.AADHAAR)
        }

        binding.cardChequeDeposit.setOnClickListener {
            com.atmplus.utils.AppLogger.i("WelcomeFragment - Cheque Deposit clicked")
            viewModel.navigateTo(ScreenState.CHEQUE_DEPOSIT)
        }

        binding.cardHome.setOnClickListener {
            com.atmplus.utils.AppLogger.i("WelcomeFragment - Home clicked, finishing activity")
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
