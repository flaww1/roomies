package pt.ipca.roomies.ui.authentication.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.roomies.R
import pt.ipca.roomies.data.repositories.RegistrationRepository
import pt.ipca.roomies.databinding.FragmentRegistrationBinding


class RegistrationFragment : Fragment() {
    private lateinit var userId: String // Declare userId here

    private val viewModel: RegistrationViewModel by viewModels()
    private val registrationRepository = RegistrationRepository()

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
        viewModel.user.observe(viewLifecycleOwner) { user ->
            userId = user?.userId ?: ""
            Log.d("RegistrationFragment", "Obtained userId: $userId")
        }

        binding.nextButton.setOnClickListener {
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                // Call the function in pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel to set up initial user data
                viewModel.register(firstName, lastName, email, password)

                // Navigate to the next step in the registration process
                registerUserWithEmailAndPassword(email, password)
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
    private fun registerUserWithEmailAndPassword(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Registration successful, obtain the userId from the authenticated user
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        // Store user information in Firestore during the initial registration
                        registrationRepository.storeUserInFirestore(userId, email, password, binding.editTextFirstName.text.toString(), binding.editTextLastName.text.toString())

                        // Update the user in the ViewModel with the obtained userId
                        viewModel.updateUserId(userId)

                        // Log the userId for debugging
                        Log.d("RegistrationFragment", "Obtained userId: $userId")

                        // Navigate to the next step in the registration process
                        navigateToRoleSelectionFragment()
                    } else {
                        // Handle the case where userId is null
                        Log.e("RegistrationFragment", "userId is null after successful registration")
                    }
                } else {
                    // Registration failed, handle the error
                    val exception = task.exception
                    // Handle the exception and show an error message
                    viewModel.handleRegistrationError(exception?.message)
                    Log.e("RegistrationFragment", "Registration failed: ${exception?.message}")
                }
            }
    }



}