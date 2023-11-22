package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import UserProfile
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentRegistrationUserProfileInfoBinding
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel

class RegistrationUserProfileInfoFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel


    private var _binding: FragmentRegistrationUserProfileInfoBinding? = null
    private val binding get() = _binding!!

    private val requiredFields by lazy {
        listOf(
            binding.editTextFirstName,
            binding.editTextLastName,
            binding.editTextBirthDate,
            binding.editTextLocation
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentRegistrationUserProfileInfoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(RegistrationViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fun <T : Enum<T>> setupSpinner(spinner: Spinner, values: Array<T>) {
            val enumStringValues = values.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, enumStringValues)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        // Initialize spinners with enum values
        setupSpinner(binding.spinnerGender, Gender.values())
        setupSpinner(binding.spinnerPronouns, Pronouns.values())
        setupSpinner(binding.spinnerOccupation, Occupation.values())

        // Implement UI interactions
        binding.buttonNext.setOnClickListener {
            // Navigate to the next fragment (you might want to validate input here)
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val birthDate = binding.editTextBirthDate.text.toString()
            val location = binding.editTextLocation.text.toString()
            val gender = binding.spinnerGender.selectedItem.toString()
            val pronouns = binding.spinnerPronouns.selectedItem.toString()
            val occupation = binding.spinnerOccupation.selectedItem.toString()
            val userProfile = UserProfile(firstName, lastName, birthDate, location, gender, pronouns, occupation)
            viewModel.updateUserProfile(userProfile)
            findNavController().navigate(R.id.action_registrationUserProfileInfoFragment_to_userInterestsFragment)
        }

        binding.buttonBack.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }

        // Enable/disable Next button based on EditText inputs and spinners
        for (field in requiredFields) {
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateNextButtonState()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        binding.spinnerGender.setOnItemSelectedListener { updateNextButtonState() }
        binding.spinnerPronouns.setOnItemSelectedListener { updateNextButtonState() }
        binding.spinnerOccupation.setOnItemSelectedListener { updateNextButtonState() }

    }


    private fun setupSpinner(spinner: Spinner, values: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateNextButtonState() {
        binding.buttonNext.isEnabled = areFieldsFilled(requiredFields) &&
                binding.spinnerGender.selectedItemPosition != Spinner.INVALID_POSITION &&
                binding.spinnerPronouns.selectedItemPosition != Spinner.INVALID_POSITION &&
                binding.spinnerOccupation.selectedItemPosition != Spinner.INVALID_POSITION
    }

    private fun areFieldsFilled(fields: List<EditText>): Boolean {
        return fields.all { it.text.isNotBlank() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun Spinner.setOnItemSelectedListener(function: () -> Unit) {

}
