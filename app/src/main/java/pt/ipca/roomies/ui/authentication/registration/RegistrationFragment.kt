package pt.ipca.roomies.ui.authentication.registration

import RegistrationViewModel
import User
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentRegistrationBinding
import pt.ipca.roomies.ui.authentication.UserViewModel

class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                viewModel.register(firstName, lastName, email, password)

                navigateToRoleSelectionFragment()
            }
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // Use navigateUp instead of popBackStack
        }

        // Add text change listeners to the fields to enable/disable the "Next" button
        binding.editTextFirstName.addTextChangedListener { updateNextButtonState() }
        binding.editTextLastName.addTextChangedListener { updateNextButtonState() }
        binding.editTextEmail.addTextChangedListener { updateNextButtonState() }
        binding.editTextPassword.addTextChangedListener { updateNextButtonState() }
        binding.editTextConfirmPassword.addTextChangedListener { updateNextButtonState() }
    }

    private fun updateNextButtonState() {
        // Enable the "Next" button only if all fields are non-empty
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        val isAnyFieldEmpty = firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()

        binding.nextButton.isEnabled = !isAnyFieldEmpty
    }

    private fun navigateToRoleSelectionFragment() {
        findNavController().navigate(R.id.action_registrationFragment_to_registrationRoleSelectionFragment)
    }


    private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Validate first name and last name (allow only letters)
        val nameRegex = "[a-zA-Z]+"
        val isFirstNameValid = firstName.matches(nameRegex.toRegex())
        val isLastNameValid = lastName.matches(nameRegex.toRegex())

        if (!isFirstNameValid) {
            binding.textInputLayoutFirstName.error = "Invalid first name"
        } else {
            binding.textInputLayoutFirstName.error = null
        }

        if (!isLastNameValid) {
            binding.textInputLayoutLastName.error = "Invalid last name"
        } else {
            binding.textInputLayoutLastName.error = null
        }

        // Validate email
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailValid) {
            binding.textInputLayoutEmail.error = "Invalid email address"
        } else {
            binding.textInputLayoutEmail.error = null
        }

        // Validate password and confirm password (ensure they match)
        val isPasswordValid = password.isNotEmpty() && password == confirmPassword
        if (!isPasswordValid) {
            binding.textInputLayoutPassword.error = "Passwords must match"
            binding.textInputLayoutConfirmPassword.error = "Passwords must match"
        } else {
            binding.textInputLayoutPassword.error = null
            binding.textInputLayoutConfirmPassword.error = null
        }

        // Return true only if all validations pass
        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid
    }



    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.reset()
        _binding = null
    }
}