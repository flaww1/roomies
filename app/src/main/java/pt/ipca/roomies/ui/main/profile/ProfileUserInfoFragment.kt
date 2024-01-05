package pt.ipca.roomies.ui.main.profile

import pt.ipca.roomies.data.repositories.RoomRepository
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jaredrummler.materialspinner.MaterialSpinner
import pt.ipca.roomies.data.entities.Occupation
import pt.ipca.roomies.data.entities.Gender
import pt.ipca.roomies.data.entities.UserProfile
import android.app.Activity.RESULT_OK
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import pt.ipca.roomies.R
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.ProfileRepository
import pt.ipca.roomies.data.repositories.RegistrationRepository
import pt.ipca.roomies.data.repositories.RegistrationViewModelFactory
import pt.ipca.roomies.databinding.FragmentProfileUserInfoBinding
import java.util.Calendar




class ProfileUserInfoFragment : Fragment() {

    private val viewModelRegistration by viewModels<RegistrationViewModel> {
        RegistrationViewModelFactory(registrationRepository)
    }


    private var _binding: FragmentProfileUserInfoBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val profileRepository by lazy {
        val userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
        ProfileRepository(userProfileDao)
    }
    private val registrationRepository by lazy {
        RegistrationRepository(requireContext())
    }


    private val locationsInPortugal = listOf("Porto", "Lisbon", "Faro", "Coimbra", "Braga", "Funchal", "Evora", "Aveiro", "Viseu")


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    selectedImageUri = uri
                    Glide.with(this).load(selectedImageUri).into(binding.imageViewProfilePicture)
                    updateNextButtonState()
                }
            }
        }

    private val requiredEditTextFields: List<EditText> by lazy {
        listOf(
            binding.editTextBirthdate,
            binding.autoCompleteTextViewLocation // Extracting the EditText from AutoCompleteTextView
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileUserInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, locationsInPortugal)
        binding.autoCompleteTextViewLocation.setAdapter(adapter)
        setupSpinners()
        binding.buttonPickProfilePicture.setOnClickListener {
            openFilePicker()
        }
        binding.autoCompleteTextViewLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.autoCompleteTextViewLocation.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)
            // Do something with the selected location
        }

        // Observe changes in the selected image URI
        viewModelRegistration.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                Glide.with(this).load(uri).into(binding.imageViewProfilePicture)
                updateNextButtonState()
            }
        }

        binding.nextButton.setOnClickListener {
            if (validateInputs()) {
                saveUserProfileAndNavigateToNextFragment()
            }
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupTextChangeListeners()
        setupSpinnerListeners()
        setupDatePicker()
    }

    private fun setupSpinners() {
        setupSpinner(binding.spinnerGender, Gender.values())
        setupSpinner(binding.spinnerOccupation, Occupation.values())
    }

    private fun setupSpinner(spinner: MaterialSpinner, values: Array<out Enum<*>>) {
        val stringValues = values.map { it.name }
        spinner.setItems(stringValues)
    }

    private fun setupSpinnerListeners() {
        binding.spinnerGender.setOnItemSelectedListener { _, _, _, _ -> updateNextButtonState() }

        binding.spinnerOccupation.setOnItemSelectedListener { _, _, _, _ -> updateNextButtonState() }
    }

    private fun setupTextChangeListeners() {
        for (field in requiredEditTextFields) {
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
        return areFieldsFilled(requiredEditTextFields) &&
                binding.spinnerGender.selectedIndex != Spinner.INVALID_POSITION &&
                binding.spinnerOccupation.selectedIndex != Spinner.INVALID_POSITION &&
                selectedImageUri != null
    }

    private fun areFieldsFilled(fields: List<EditText>): Boolean {
        return fields.all { it.text.isNotBlank() }
    }

    private fun updateNextButtonState() {
        binding.nextButton.isEnabled = validateInputs()
    }

    private fun saveUserProfileAndNavigateToNextFragment() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val birthDate = binding.editTextBirthdate.text.toString()
        val location = binding.autoCompleteTextViewLocation.text.toString()
        val gender = binding.spinnerGender.getItems<String>()[binding.spinnerGender.selectedIndex]
        val occupation = binding.spinnerOccupation.getItems<String>()[binding.spinnerOccupation.selectedIndex]
        val bio = binding.editTextBio.text.toString()

        if (userId != null && selectedImageUri != null) {

            // Update the user profile picture URL in Firestore
            profileRepository.updateProfilePictureUrl(userId, selectedImageUri.toString())

            // Create the pt.ipca.roomies.data.entities.UserProfile object with the updated profile picture URL
            val userProfile = UserProfile(
                userId = userId,
                profilePictureUrl = selectedImageUri.toString(),
                location = location,
                bio = bio,
                gender = gender,
                occupation = occupation,
                birthDate = birthDate
            )

            profileRepository.createUserProfile(
                userProfile = userProfile,
                onSuccess = {
                    // Navigate to the next fragment
                    findNavController().navigate(R.id.action_profileUserInfoFragment_to_profileUserInterestsFragment)
                },
                onFailure = { e ->
                    Toast.makeText(requireContext(), "Error saving user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Handle the case where the user is not authenticated or selectedImageUri is null
            Toast.makeText(requireContext(), "User not authenticated or no image selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }




    private fun setupDatePicker() {
        binding.editTextBirthdate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year)
                binding.editTextBirthdate.setText(selectedDate)
                updateNextButtonState()
            },
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )

        // Set date range to allow only users aged 18 and above
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -18)
        dpd.maxDate = maxDate
        dpd.show(requireActivity().supportFragmentManager, "Datepickerdialog")
    }

}
