package pt.ipca.roomies.ui.main.landlord.habitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.*

class CreateHabitationFragment : Fragment() {

    private lateinit var viewModel: HabitationViewModel // Replace with your actual ViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_habitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HabitationViewModel::class.java) // Replace with your actual ViewModel
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
        // Set up the spinners with appropriate adapters and data
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Cities.values())
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        val habitationTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, HabitationType.values())
        habitationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        habitationTypeSpinner.adapter = habitationTypeAdapter

        val smokingPolicyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, SmokingPolicies.values())
        smokingPolicyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        smokingPolicySpinner.adapter = smokingPolicyAdapter

        val noiseLevelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, NoiseLevels.values())
        noiseLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        noiseLevelSpinner.adapter = noiseLevelAdapter

        val guestPolicyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, GuestPolicies.values())
        guestPolicyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        guestPolicySpinner.adapter = guestPolicyAdapter
    }

    private fun setupButtons() {
        createHabitationButton.isEnabled = false // Initially, create button is disabled

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
            val petsAllowed = petsAllowedCheckBox.isChecked
            val smokingPolicy = smokingPolicySpinner.selectedItem as SmokingPolicies
            val noiseLevel = noiseLevelSpinner.selectedItem as NoiseLevels
            val guestPolicy = guestPolicySpinner.selectedItem as GuestPolicies
            val securityCameras = securityCamerasCheckBox.isChecked
            val lockedEntrance = lockedEntranceCheckBox.isChecked
            val securityGuard = securityGuardCheckBox.isChecked
            val codedEntrance = codedEntranceCheckBox.isChecked
            val cardedEntrance = cardedEntranceCheckBox.isChecked

            // Enable or disable create button based on input validation
            createHabitationButton.isEnabled = isInputValid(address, numberOfRooms, numberOfBathrooms, description)

            // Create a Habitation object with the retrieved data
            val habitation = Habitation(
                habitationId = "", // You can generate a unique ID or leave it empty for Firestore to generate one
                landlordId = "", // Replace with the actual landlord ID
                address = address,
                city = city,
                numberOfRooms = numberOfRooms,
                numberOfBathrooms = numberOfBathrooms,
                habitationType = habitationType,
                description = description,
                habitationAmenities = HabitationAmenities(internet, parking, kitchen, laundry),
                petsAllowed = petsAllowed,
                smokingPolicy = smokingPolicy,
                noiseLevel = noiseLevel,
                guestPolicy = guestPolicy,
                securityMeasures = SecurityMeasures(securityCameras, lockedEntrance, securityGuard, codedEntrance, cardedEntrance),
                tenants = emptyList() // Initially, there are no tenants
            )

            // Call the ViewModel function to create the habitation
            viewModel.createHabitation(habitation)
        }

        backButton.setOnClickListener {
            // Handle back button click (navigate back to the main screen)
            // Implement navigation logic as needed
        }
    }

    private fun isInputValid(address: String, numberOfRooms: Int?, numberOfBathrooms: Int?, description: String): Boolean {
        // Implement your input validation logic here
        return !address.isBlank() && numberOfRooms != null && numberOfRooms > 0 && numberOfBathrooms != null && numberOfBathrooms > 0 && !description.isBlank()
    }
}
