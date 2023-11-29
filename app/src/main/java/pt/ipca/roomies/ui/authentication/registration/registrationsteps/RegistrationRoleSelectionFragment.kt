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

        binding.nextButton.setOnClickListener {
            val role = if (binding.landlordRadioButton.isChecked) "Landlord" else "User"
            viewModel.updateUserRole(role)
            navigateToUserProfileInfo()
        }

        // Enable/disable Next button based on RadioButton selection
        binding.roleSelectionGroup.setOnCheckedChangeListener { _, _ ->
            binding.nextButton.isEnabled = binding.landlordRadioButton.isChecked || binding.userRadioButton.isChecked
        }
    }

    private fun navigateToUserProfileInfo() {
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

