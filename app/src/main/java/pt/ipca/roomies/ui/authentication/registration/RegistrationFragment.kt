package pt.ipca.roomies.ui.authentication.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pt.ipca.roomies.databinding.FragmentRegistrationBinding

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

        // Set up UI interactions and observe ViewModel
        binding.buttonRegister.setOnClickListener {
            // Retrieve input values
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            // Validate and perform registration logic
            if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                viewModel.register(firstName, lastName, email, password)
            }
        }
    }

    private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Implement your validation logic here
        // Return true if inputs are valid, false otherwise
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}