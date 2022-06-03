package com.example.snigdhaapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.snigdhaapp.ViewModel.AuthViewModel
import com.example.snigdhaapp.compose.SignUP
import com.example.snigdhaapp.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      _binding = FragmentSecondBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeSignup.setContent {
            activity?.let { AuthViewModel(it.application) }
                ?.let { SignUP(viewModel = it,onNavigate = { dest -> findNavController().navigate(dest) }) }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}