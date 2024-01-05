// RoleSelectionFragment.kt
package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.data.repositories.RegistrationRepository
import pt.ipca.roomies.data.repositories.RegistrationViewModelFactory
import pt.ipca.roomies.databinding.FragmentRegistrationRoleSelectionBinding



class RegistrationRoleSelectionFragment : Fragment() {

    private var _binding: FragmentRegistrationRoleSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(
            registrationRepository = RegistrationRepository(requireContext())
        )

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationRoleSelectionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            val role = if (binding.landlordRadioButton.isChecked) "Landlord" else "User"
            Log.d("RoleSelectionFragment", "Selected Role: $role")

            viewModel.updateUserRole(role)

            navigateToHomeFragment()
        }

        // Enable/disable Next button based on RadioButton selection
        binding.roleSelectionGroup.setOnCheckedChangeListener { _, _ ->
            binding.nextButton.isEnabled = binding.landlordRadioButton.isChecked || binding.userRadioButton.isChecked
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_registrationRoleSelectionFragment_to_homeFragment)
    }




}

