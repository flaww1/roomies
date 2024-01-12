package pt.ipca.roomies.ui.main.users.habitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.data.entities.*
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory
import pt.ipca.roomies.databinding.FragmentEditHabitationBinding

class EditHabitationFragment : Fragment() {

    private lateinit var habitationDao: HabitationDao
    private val viewModel: HabitationViewModel by viewModels {
        HabitationViewModelFactory(habitationDao)
    }

    private lateinit var binding: FragmentEditHabitationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditHabitationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitationDao = AppDatabase.getDatabase(requireContext()).habitationDao()

        // Retrieve arguments passed from the navigation component
        val address = arguments?.getString("address", "")
        val city = arguments?.getString("city", "")
        val numberOfRooms = arguments?.getInt("numberOfRooms", 0)
        val numberOfBathrooms = arguments?.getInt("numberOfBathrooms", 0)
        val habitationTypeString = arguments?.getString("habitationType", "")
        val habitationType = try {
            if (habitationTypeString != null) {
                HabitationType.valueOf(habitationTypeString)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            // Handle the case when the string does not match any enum constant
            // For now, we'll log an error, but you can customize this based on your needs
            Log.e("EditHabitationFragment", "Invalid HabitationType: $habitationTypeString", e)
            null
        }
        val description = arguments?.getString("description", "")
        val internet = arguments?.getBoolean("internet", false) ?: false
        val parking = arguments?.getBoolean("parking", false) ?: false
        val kitchen = arguments?.getBoolean("kitchen", false) ?: false
        val laundry = arguments?.getBoolean("laundry", false) ?: false
        val petsAllowed = arguments?.getBoolean("petsAllowed", false) ?: false
        val smokingPolicyString = arguments?.getString("smokingPolicy", "")
        val smokingPolicy = try {
            if (smokingPolicyString != null) {
                SmokingPolicies.valueOf(smokingPolicyString)
            } else {

            }
        } catch (e: IllegalArgumentException) {
            // Handle the case when the string does not match any enum constant
            // For now, we'll log an error, but you can customize this based on your needs
            Log.e("EditHabitationFragment", "Invalid SmokingPolicies: $smokingPolicyString", e)
            null
        }
        val noiseLevelString = arguments?.getString("noiseLevel", "")
        val noiseLevel = try {
            if (noiseLevelString != null) {
                NoiseLevels.valueOf(noiseLevelString)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            Log.e("EditHabitationFragment", "Invalid NoiseLevels: $noiseLevelString", e)
            null
        }

        val guestPolicyString = arguments?.getString("guestPolicy", "")
        val guestPolicy = try {
            if (guestPolicyString != null) {
                GuestPolicies.valueOf(guestPolicyString)
            } else {
                null
            }
        } catch (e: IllegalArgumentException) {
            Log.e("EditHabitationFragment", "Invalid GuestPolicies: $guestPolicyString", e)
            null
        }
        val securityCameras = arguments?.getBoolean("securityCameras", false) ?: false
        val securityGuards = arguments?.getBoolean("securityGuards", false) ?: false
        val cardedEntrance = arguments?.getBoolean("cardedEntrance", false) ?: false
        val codedEntrance = arguments?.getBoolean("codedEntrance", false) ?: false
        val lockedEntrance = arguments?.getBoolean("lockedEntrance", false) ?: false


        // Populate UI fields with retrieved arguments
        binding.editTextAddress.setText(address)
        binding.spinnerCity.setSelection(Cities.entries.toTypedArray().indexOfFirst { it.city == city })
        binding.editTextNumberOfRooms.setText(numberOfRooms.toString())
        binding.editTextNumberOfBathrooms.setText(numberOfBathrooms.toString())
        binding.spinnerHabitationType.setSelection(HabitationType.entries.indexOf(habitationType))
        binding.editTextDescription.setText(description)
        binding.checkBoxInternet.text = internet.toString()
        binding.checkBoxParking.text = parking.toString()
        binding.checkBoxKitchen.text = kitchen.toString()
        binding.checkBoxLaundry.text = laundry.toString()
        binding.checkBoxPetsAllowed.text = petsAllowed.toString()
        binding.spinnerSmokingPolicy.setSelection(SmokingPolicies.entries.indexOf(smokingPolicy))
        binding.spinnerNoiseLevel.setSelection(NoiseLevels.entries.indexOf(noiseLevel))
        binding.spinnerGuestPolicy.setSelection(GuestPolicies.entries.indexOf(guestPolicy))
        binding.checkBoxSecurityCameras.text = securityCameras.toString()
        binding.checkBoxSecurityGuard.text = securityGuards.toString()
        binding.checkBoxCardedEntrance.text = cardedEntrance.toString()
        binding.checkBoxCodedEntrance.text = codedEntrance.toString()
        binding.checkBoxLockedEntrance.text = lockedEntrance.toString()


        // Populate other UI fields as needed

        setupButtons()
    }

    private fun setupButtons() {
        binding.updateHabitationButton.setOnClickListener {
            // Retrieve data from UI elements
            val editedAddress = binding.editTextAddress.text.toString()
            val editedCity = binding.spinnerCity.selectedItem.toString()
            val editedNumberOfRooms = binding.editTextNumberOfRooms.text.toString().toInt()
            val editedNumberOfBathrooms = binding.editTextNumberOfBathrooms.text.toString().toInt()
            val editedHabitationType = binding.spinnerHabitationType.selectedItem.toString()
            val editedDescription = binding.editTextDescription.text.toString()
            val editedInternet = binding.checkBoxInternet.isChecked
            val editedParking = binding.checkBoxParking.isChecked
            val editedKitchen = binding.checkBoxKitchen.isChecked
            val editedLaundry = binding.checkBoxLaundry.isChecked
            val editedPetsAllowed = binding.checkBoxPetsAllowed.isChecked
            val editedSmokingPolicy = binding.spinnerSmokingPolicy.selectedItem.toString()
            val editedNoiseLevel = binding.spinnerNoiseLevel.selectedItem.toString()
            val editedGuestPolicy = binding.spinnerGuestPolicy.selectedItem.toString()
            val editedSecurityCameras = binding.checkBoxSecurityCameras.isChecked
            val editedSecurityGuards = binding.checkBoxSecurityGuard.isChecked
            val editedCardedEntrance = binding.checkBoxCardedEntrance.isChecked
            val editedCodedEntrance = binding.checkBoxCodedEntrance.isChecked
            val editedLockedEntrance = binding.checkBoxLockedEntrance.isChecked




            // Retrieve other edited data as needed

            val updatedHabitation = viewModel.selectedHabitation.value?.copy(
                habitationId = viewModel.selectedHabitation.value?.habitationId ?: "", // Ensure you provide the existing habitationId
                address = editedAddress,
                city = Cities.valueOf(editedCity),
                numberOfRooms = editedNumberOfRooms,
                numberOfBathrooms = editedNumberOfBathrooms,
                habitationType = HabitationType.valueOf(editedHabitationType),
                description = editedDescription,
                habitationAmenities = listOf(
                    HabitationAmenities.INTERNET to editedInternet,
                    HabitationAmenities.PARKING to editedParking,
                    HabitationAmenities.KITCHEN to editedKitchen,
                    HabitationAmenities.LAUNDRY to editedLaundry,
                ).filter { it.second }.map { it.first },
                securityMeasures = listOf(
                    SecurityMeasures.SECURITY_CAMERAS to editedSecurityCameras,
                    SecurityMeasures.CARDED_ENTRANCE to editedCardedEntrance,
                    SecurityMeasures.CODED_ENTRANCE to editedCodedEntrance,

                ).filter { it.second }.map { it.first },
                smokingPolicy = SmokingPolicies.valueOf(editedSmokingPolicy),
                noiseLevel = NoiseLevels.valueOf(editedNoiseLevel),
                guestPolicy = GuestPolicies.valueOf(editedGuestPolicy)
            )


            if (updatedHabitation != null) {
                val habitationId = updatedHabitation.habitationId // Replace 'id' with the actual property name
                viewModel.updateHabitation(habitationId, updatedHabitation)


                Log.d("EditHabitationFragment", "Address: $editedAddress")
                Log.d("EditHabitationFragment", "City: $editedCity")
                Log.d("EditHabitationFragment", "Number of Rooms: $editedNumberOfRooms")
                Log.d("EditHabitationFragment", "Number of Bathrooms: $editedNumberOfBathrooms")
                Log.d("EditHabitationFragment", "Habitation Type: $editedHabitationType")
                Log.d("EditHabitationFragment", "Description: $editedDescription")
                Log.d("EditHabitationFragment", "Internet: $editedInternet")
                Log.d("EditHabitationFragment", "Parking: $editedParking")
                Log.d("EditHabitationFragment", "Kitchen: $editedKitchen")
                Log.d("EditHabitationFragment", "Laundry: $editedLaundry")
                Log.d("EditHabitationFragment", "Pets Allowed: $editedPetsAllowed")
                Log.d("EditHabitationFragment", "Smoking Policy: $editedSmokingPolicy")
                Log.d("EditHabitationFragment", "Noise Level: $editedNoiseLevel")
                Log.d("EditHabitationFragment", "Guest Policy: $editedGuestPolicy")
                Log.d("EditHabitationFragment", "Security Cameras: $editedSecurityCameras")
                Log.d("EditHabitationFragment", "Security Guards: $editedSecurityGuards")
                Log.d("EditHabitationFragment", "Carded Entrance: $editedCardedEntrance")
                Log.d("EditHabitationFragment", "Coded Entrance: $editedCodedEntrance")
                Log.d("EditHabitationFragment", "Locked Entrance: $editedLockedEntrance")

            }


        }

        // Handle the back button click
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}
