package pt.ipca.roomies.ui.authentication.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.repositories.RegistrationRepository
import pt.ipca.roomies.data.repositories.RegistrationViewModelFactory
import pt.ipca.roomies.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private lateinit var userId: String
    private lateinit var registrationRepository: RegistrationRepository

    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(registrationRepository)
    }

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

        registrationRepository = RegistrationRepository(requireContext())

        viewModel.user.observe(viewLifecycleOwner) { user ->
            userId = user?.userId ?: ""
            Log.d("RegistrationFragment", "Obtained userId: $userId")
        }

        // Inside your onViewCreated function or wherever appropriate
        binding.nextButton.setOnClickListener {
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            val selectedRole = when {
                binding.landlordRadioButton.isChecked -> "Landlord"
                binding.userRadioButton.isChecked -> "User"
                else -> "DefaultRole" // Set a default role if none is selected
            }

            if (validateInputs(firstName, lastName, email, password, confirmPassword, selectedRole)) {

                // Call the function in RegistrationViewModel with the selected role
                viewModel.register(firstName, lastName, email, password, selectedRole)

                // Pass the selected role to registerUserWithEmailAndPassword
                lifecycleScope.launch {
                    registerUserWithEmailAndPassword(email, password, firstName, lastName, selectedRole)
                }
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

        viewModel.userId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                // Update UI or perform actions based on the obtained userId
                Log.d("RegistrationFragment", "Obtained userId: $userId")
            }
        }


    }

    private fun updateNextButtonState() {
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val selectedRole = when {
            binding.landlordRadioButton.isChecked -> "Landlord"
            binding.userRadioButton.isChecked -> "User"
            else -> "DefaultRole" // Set a default role if none is selected
        }

        binding.nextButton.isEnabled =
            validateInputs(firstName, lastName, email, password, confirmPassword, selectedRole)
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
    }

    private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        selectedRole: String
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

    private suspend fun registerUserWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        selectedRole: String
    ) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        lifecycleScope.launch {
                            // Store user information in Firestore during the initial registration
                            viewModel.updateUserId(userId)

                            // Store user data in Firestore
                            registrationRepository.storeUserInFirestore(
                                userId,
                                email,
                                password,
                                firstName,
                                lastName,
                                selectedRole
                            )

                            // Navigate to the next step in the registration process
                            navigateToHomeFragment()
                        }
                    } else {
                        Log.e("RegistrationFragment", "userId is null after successful registration")
                    }
                } else {
                    val exception = task.exception
                    viewModel.handleRegistrationError(exception?.message)
                    Log.e("RegistrationFragment", "Registration failed: ${exception?.message}")
                }
            }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
