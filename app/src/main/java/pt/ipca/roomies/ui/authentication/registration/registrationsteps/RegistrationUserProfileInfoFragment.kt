package pt.ipca.roomies.ui.authentication.registration.registrationsteps

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
import Occupation
import Pronouns
import Gender
import UserProfile
import RegistrationViewModel
import android.widget.AdapterView
import com.jaredrummler.materialspinner.MaterialSpinner

class RegistrationUserProfileInfoFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel
    private var _binding: FragmentRegistrationUserProfileInfoBinding? = null
    private val binding get() = _binding!!

    private val requiredFields by lazy {
        listOf(
            binding.editTextFirstName,
            binding.editTextLastName,
            binding.editTextBirthdate,
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

        setupSpinners()

        binding.buttonNext.setOnClickListener {
            if (validateInputs()) {
                navigateToUserInterestsFragment()
            }
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupTextChangeListeners()
        setupSpinnerListeners()
    }

    private fun setupSpinners() {
        setupSpinner(binding.spinnerGender, Gender.values())
        setupSpinner(binding.spinnerPronouns, Pronouns.values())
        setupSpinner(binding.spinnerOccupation, Occupation.values())
    }

    private fun setupSpinner(spinner: MaterialSpinner, values: Array<out Enum<*>>) {
        val stringValues = values.map { it.name }
        spinner.setItems(stringValues)
    }

    private fun setupSpinnerListeners() {
        binding.spinnerGender.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<String> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
                updateNextButtonState()
            }

            fun onNothingSelected(parent: MaterialSpinner?) {
                // Do nothing or handle the case when nothing is selected
            }
        })

        binding.spinnerPronouns.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<String> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
                updateNextButtonState()
            }

            fun onNothingSelected(parent: MaterialSpinner?) {
                // Do nothing or handle the case when nothing is selected
            }
        })

        binding.spinnerOccupation.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<String> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
                updateNextButtonState()
            }

            fun onNothingSelected(parent: MaterialSpinner?) {
                // Do nothing or handle the case when nothing is selected
            }
        })
    }

    private fun Spinner.setOnItemSelectedListener(listener: (position: Int) -> Unit) {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }
    }
    private fun setupTextChangeListeners() {
        for (field in requiredFields) {
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateNextButtonState()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun validateInputs(): Boolean {
        return areFieldsFilled(requiredFields) &&
                binding.spinnerGender.selectedIndex != -1 &&
                binding.spinnerPronouns.selectedIndex != -1 &&
                binding.spinnerOccupation.selectedIndex != -1
    }


    private fun areFieldsFilled(fields: List<EditText>): Boolean {
        return fields.all { it.text.isNotBlank() }
    }

    private fun updateNextButtonState() {
        binding.buttonNext.isEnabled = validateInputs()
    }

    private fun navigateToUserInterestsFragment() {
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val birthDate = binding.editTextBirthdate.text.toString()
        val location = binding.editTextLocation.text.toString()
        val gender = binding.spinnerGender.getItems<String>()[binding.spinnerGender.selectedIndex]
        val pronouns = binding.spinnerPronouns.getItems<String>()[binding.spinnerPronouns.selectedIndex]
        val occupation = binding.spinnerOccupation.getItems<String>()[binding.spinnerOccupation.selectedIndex]

        // For now, provide placeholder values for userId and profilePictureUrl
        val userProfile = UserProfile(
            userProfileId = "placeholder_id",
            userId = "placeholder_user_id",
            profilePictureUrl = "placeholder_url",
            location = location,
            bio = "", // Add bio field if needed
            pronouns = pronouns,
            gender = gender,
            occupation = occupation,
            selectedTags = emptyList()
        )
        viewModel.updateUserProfile(userProfile)
        findNavController().navigate(R.id.action_registrationUserProfileInfoFragment_to_userInterestsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



