package pt.ipca.roomies.ui.main.landlord.habitation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.*
import pt.ipca.roomies.databinding.FragmentCreateHabitationBinding

class CreateHabitationFragment : Fragment() {

    private val viewModel: HabitationViewModel by lazy {
        ViewModelProvider(this)[HabitationViewModel::class.java]
    }
    private lateinit var addressEditText: EditText
    private lateinit var citySpinner: Spinner
    private lateinit var numberOfRoomsEditText: EditText
    private lateinit var numberOfBathroomsEditText: EditText
    private lateinit var habitationTypeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var internetCheckBox: CheckBox
    private lateinit var parkingCheckBox: CheckBox
    private lateinit var kitchenCheckBox: CheckBox
    private lateinit var laundryCheckBox: CheckBox
    private lateinit var petsAllowedCheckBox: CheckBox
    private lateinit var smokingPolicySpinner: Spinner
    private lateinit var noiseLevelSpinner: Spinner
    private lateinit var guestPolicySpinner: Spinner
    private lateinit var securityCamerasCheckBox: CheckBox
    private lateinit var lockedEntranceCheckBox: CheckBox
    private lateinit var securityGuardCheckBox: CheckBox
    private lateinit var codedEntranceCheckBox: CheckBox
    private lateinit var cardedEntranceCheckBox: CheckBox
    private lateinit var createHabitationButton: Button
    private lateinit var backButton: Button
    private lateinit var binding: FragmentCreateHabitationBinding

    private val habitationViewModel: HabitationViewModel by lazy {
        ViewModelProvider(requireActivity())[HabitationViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateHabitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressEditText = view.findViewById(R.id.editTextAddress)
        citySpinner = view.findViewById(R.id.spinnerCity)
        numberOfRoomsEditText = view.findViewById(R.id.editTextNumberOfRooms)
        numberOfBathroomsEditText = view.findViewById(R.id.editTextNumberOfBathrooms)
        habitationTypeSpinner = view.findViewById(R.id.spinnerHabitationType)
        descriptionEditText = view.findViewById(R.id.editTextDescription)
        internetCheckBox = view.findViewById(R.id.checkBoxInternet)
        parkingCheckBox = view.findViewById(R.id.checkBoxParking)
        kitchenCheckBox = view.findViewById(R.id.checkBoxKitchen)
        laundryCheckBox = view.findViewById(R.id.checkBoxLaundry)
        petsAllowedCheckBox = view.findViewById(R.id.checkBoxPetsAllowed)
        smokingPolicySpinner = view.findViewById(R.id.spinnerSmokingPolicy)
        noiseLevelSpinner = view.findViewById(R.id.spinnerNoiseLevel)
        guestPolicySpinner = view.findViewById(R.id.spinnerGuestPolicy)
        securityCamerasCheckBox = view.findViewById(R.id.checkBoxSecurityCameras)
        lockedEntranceCheckBox = view.findViewById(R.id.checkBoxLockedEntrance)
        securityGuardCheckBox = view.findViewById(R.id.checkBoxSecurityGuard)
        codedEntranceCheckBox = view.findViewById(R.id.checkBoxCodedEntrance)
        cardedEntranceCheckBox = view.findViewById(R.id.checkBoxCardedEntrance)
        createHabitationButton = view.findViewById(R.id.createHabitationButton)
        backButton = view.findViewById(R.id.backButton)

        setupSpinners()
        setupButtons()
    }

    private fun setupSpinners() {
        citySpinner.adapter = createEnumAdapter(Cities.values())
        habitationTypeSpinner.adapter = createEnumAdapter(HabitationType.values())
        smokingPolicySpinner.adapter = createEnumAdapter(SmokingPolicies.values())
        noiseLevelSpinner.adapter = createEnumAdapter(NoiseLevels.values())
        guestPolicySpinner.adapter = createEnumAdapter(GuestPolicies.values())
    }




    private fun setupButtons() {
        createHabitationButton.isEnabled = false // Initially, create button is disabled
        addressEditText.addTextChangedListener(getTextWatcher())
        numberOfRoomsEditText.addTextChangedListener(getTextWatcher())
        numberOfBathroomsEditText.addTextChangedListener(getTextWatcher())
        descriptionEditText.addTextChangedListener(getTextWatcher())

        createHabitationButton.setOnClickListener {
            // Retrieve data from UI elements
            val address = addressEditText.text.toString()
            val city = citySpinner.selectedItem as Cities
            val numberOfRooms = numberOfRoomsEditText.text.toString().toIntOrNull()
            val numberOfBathrooms = numberOfBathroomsEditText.text.toString().toIntOrNull()
            val habitationType = habitationTypeSpinner.selectedItem as HabitationType
            val description = descriptionEditText.text.toString()
            val internet = internetCheckBox.isChecked
            val parking = parkingCheckBox.isChecked
            val kitchen = kitchenCheckBox.isChecked
            val laundry = laundryCheckBox.isChecked
            val petsAllowed: Boolean = petsAllowedCheckBox.isChecked
            val smokingPolicy = smokingPolicySpinner.selectedItem as SmokingPolicies
            val noiseLevel = noiseLevelSpinner.selectedItem as NoiseLevels
            val guestPolicy = guestPolicySpinner.selectedItem as GuestPolicies
            val securityCameras = securityCamerasCheckBox.isChecked
            val lockedEntrance = lockedEntranceCheckBox.isChecked
            val securityGuard = securityGuardCheckBox.isChecked
            val codedEntrance = codedEntranceCheckBox.isChecked
            val cardedEntrance = cardedEntranceCheckBox.isChecked

            val isValid = isInputValid(address, numberOfRooms, numberOfBathrooms, description)
            createHabitationButton.isEnabled = isValid

            Log.d("CreateHabitationFragment", "Button Enabled: $isValid")
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                Log.d("CreateHabitationFragment", "Current user: ${currentUser.uid}")
                val habitation = Habitation(
                    habitationId = "", // You can generate a unique ID or leave it empty for Firestore to generate one
                    landlordId = currentUser.uid, // Replace with the actual landlord ID
                    address = address,
                    city = city,
                    numberOfRooms = numberOfRooms ?: 0, // Use the Elvis operator to provide a default value if numberOfRooms is null
                    numberOfBathrooms = numberOfBathrooms ?: 0, // Use the Elvis operator to provide a default value if numberOfBathrooms is null
                    habitationType = habitationType,
                    description = description,
                    habitationAmenities = getCheckedAmenities().toList(),
                    securityMeasures = getCheckedSecurityMeasures().toList(),
                    petsAllowed = petsAllowed, // Use the Elvis operator to provide a default value if petsAllowed is null
                    smokingPolicy = smokingPolicy,
                    noiseLevel = noiseLevel,
                    guestPolicy = guestPolicy,
                    tenants = emptyList() // Initially, there are no tenants
                )

                // Call the ViewModel function to create the habitation
                viewModel.createHabitation(habitation)
                findNavController().navigateUp()

            } else {
                // Handle user not signed in
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show()
            }

        }



        backButton.setOnClickListener {
            findNavController().navigateUp() // Use navigateUp instead of popBackStack

        }

        viewModel.habitationCreationSuccess.observe(viewLifecycleOwner) { documentId ->
            if (documentId != null) {
                Toast.makeText(
                    context,
                    "Habitation created with ID: $documentId",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Failed to create habitation", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // code before text is changed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // code when text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // code after text has changed
                createHabitationButton.isEnabled = isInputValid(
                    addressEditText.text.toString(),
                    numberOfRoomsEditText.text.toString().toIntOrNull(),
                    numberOfBathroomsEditText.text.toString().toIntOrNull(),
                    descriptionEditText.text.toString()
                )
            }
        }
    }
    private fun isInputValid(
        address: String,
        numberOfRooms: Int?,
        numberOfBathrooms: Int?,
        description: String
    ): Boolean {
        return address.isNotBlank() && numberOfRooms != null && numberOfRooms > 0 &&
                numberOfBathrooms != null && numberOfBathrooms > 0 && description.isNotBlank()
    }



    private fun getCheckedAmenities(): Set<HabitationAmenities> {
        return setOf(
            HabitationAmenities.INTERNET.takeIf { internetCheckBox.isChecked },
            HabitationAmenities.PARKING.takeIf { parkingCheckBox.isChecked },
            HabitationAmenities.KITCHEN.takeIf { kitchenCheckBox.isChecked },
            HabitationAmenities.LAUNDRY.takeIf { laundryCheckBox.isChecked }
        ).filterNotNull().toSet()
    }

    private fun getCheckedSecurityMeasures(): Set<SecurityMeasures> {
        return setOf(
            SecurityMeasures.SECURITY_CAMERAS.takeIf { securityCamerasCheckBox.isChecked },
            SecurityMeasures.KEY_ENTRANCE.takeIf { lockedEntranceCheckBox.isChecked },
            SecurityMeasures.SECURITY_GUARD.takeIf { securityGuardCheckBox.isChecked },
            SecurityMeasures.CODED_ENTRANCE.takeIf { codedEntranceCheckBox.isChecked },
            SecurityMeasures.CARDED_ENTRANCE.takeIf { cardedEntranceCheckBox.isChecked }
        ).filterNotNull().toSet()
    }

    private inline fun <reified T : Enum<T>> createEnumAdapter(values: Array<T>): ArrayAdapter<T> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

}
