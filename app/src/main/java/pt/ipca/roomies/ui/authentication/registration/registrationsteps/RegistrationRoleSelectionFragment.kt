// RoleSelectionFragment.kt
package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import RegistrationViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentRegistrationRoleSelectionBinding



class RegistrationRoleSelectionFragment : Fragment() {

    private var _binding: FragmentRegistrationRoleSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegistrationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationRoleSelectionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(RegistrationViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Implement UI interactions
        binding.nextButton.setOnClickListener {
            val role = if (binding.userCheckbox.isChecked) "User" else "Landlord"
            viewModel.updateUserRole(role)
            navigateToUserProfileInfo()
        }

        // Implement back button functionality
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }


        // Enable/disable Next button based on checkbox selection
        binding.userCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.userCheckbox.isEnabled = !isChecked
            binding.nextButton.isEnabled = isChecked || binding.userCheckbox.isChecked
        }

        binding.landlordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.landlordCheckbox.isEnabled = !isChecked
            binding.nextButton.isEnabled = isChecked || binding.landlordCheckbox.isChecked
        }
    }

    private fun navigateToUserProfileInfo() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
