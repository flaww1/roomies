package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import RegistrationViewModel
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jaredrummler.materialspinner.MaterialSpinner
import pt.ipca.roomies.R
import Occupation
import Gender
import UserProfile
import android.app.Activity.RESULT_OK
import android.widget.ArrayAdapter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import pt.ipca.roomies.databinding.FragmentRegistrationUserProfileInfoBinding
import java.util.Calendar

class RegistrationUserProfileInfoFragment : Fragment() {
    private lateinit var viewModelRegistration: RegistrationViewModel
    private var _binding: FragmentRegistrationUserProfileInfoBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    val locationsInPortugal = listOf("Porto", "Lisbon", "Faro", "Coimbra", "Braga", "Funchal", "Evora", "Aveiro", "Viseu")


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
        _binding = FragmentRegistrationUserProfileInfoBinding.inflate(inflater, container, false)
        viewModelRegistration = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]

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
            val selectedLocation = adapter.getItem(position)
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
        binding.spinnerGender.setOnItemSelectedListener(object :
            MaterialSpinner.OnItemSelectedListener<String> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
                updateNextButtonState()
            }

            fun onNothingSelected(parent: MaterialSpinner?) {
                // Do nothing or handle the case when nothing is selected
            }
        })

        binding.spinnerOccupation.setOnItemSelectedListener(object :
            MaterialSpinner.OnItemSelectedListener<String> {
            override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
                updateNextButtonState()
            }

            fun onNothingSelected(parent: MaterialSpinner?) {
                // Do nothing or handle the case when nothing is selected
            }
        })
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
        binding.buttonNext.isEnabled = validateInputs()
    }

    private fun navigateToUserInterestsFragment() {
        val birthDate = binding.editTextBirthdate.text.toString()
        val location = binding.autoCompleteTextViewLocation.text.toString()
        val gender =
            binding.spinnerGender.getItems<String>()[binding.spinnerGender.selectedIndex]
        val occupation =
            binding.spinnerOccupation.getItems<String>()[binding.spinnerOccupation.selectedIndex]

        val userProfile = UserProfile(
            userProfileId = "",
            userId = "",
            profilePictureUrl = selectedImageUri.toString(),
            location = location,
            bio = "",
            gender = gender,
            occupation = occupation,
            birthDate = birthDate,
            selectedTags = emptyList()
        )

        viewModelRegistration.updateSelectedImageUri(selectedImageUri)
        viewModelRegistration.updateUserProfile(userProfile)
        findNavController().navigate(R.id.action_registrationUserProfileInfoFragment_to_userInterestsFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
