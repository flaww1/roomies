// FragmentCreateRoom.kt
package pt.ipca.roomies.ui.main.landlord.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.LeaseDuration
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.RoomAmenities
import pt.ipca.roomies.data.entities.RoomSize
import pt.ipca.roomies.data.entities.RoomStatus
import pt.ipca.roomies.data.entities.RoomType

class CreateRoomFragment : Fragment() {

    private lateinit var viewModel: RoomViewModel

    // Declare your views here
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerRoomType: Spinner
    private lateinit var spinnerRoomSize: Spinner
    private lateinit var spinnerLeaseDuration: Spinner
    private lateinit var checkBoxAirConditioning: CheckBox
    private lateinit var checkBoxHeating: CheckBox
    private lateinit var createRoomButton: Button
    private lateinit var backButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)

        // Initialize your views
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextPrice = view.findViewById(R.id.editTextPrice)
        spinnerRoomType = view.findViewById(R.id.spinnerRoomType)
        spinnerRoomSize = view.findViewById(R.id.spinnerRoomSize)
        spinnerLeaseDuration = view.findViewById(R.id.spinnerLeaseDuration)
        checkBoxAirConditioning = view.findViewById(R.id.checkBoxAirConditioning)
        checkBoxHeating = view.findViewById(R.id.checkBoxHeating)
        createRoomButton = view.findViewById(R.id.createRoomButton)
        backButton = view.findViewById(R.id.backButton)

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        // Set up the spinners with appropriate adapters and data
        val roomTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, RoomType.values())
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoomType.adapter = roomTypeAdapter

        val roomSizeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, RoomSize.values())
        roomSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoomSize.adapter = roomSizeAdapter

        val leaseDurationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, LeaseDuration.values())
        leaseDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLeaseDuration.adapter = leaseDurationAdapter
    }

    private fun setupListeners() {
        createRoomButton.setOnClickListener {
            // Handle the logic for creating a room
            val room = Room(
                description = editTextDescription.text.toString(),
                price = editTextPrice.text.toString().toDouble(),
                roomType = spinnerRoomType.selectedItem as RoomType,
                roomSize = spinnerRoomSize.selectedItem as RoomSize,
                leaseDuration = spinnerLeaseDuration.selectedItem as LeaseDuration,
                roomAmenities = mutableListOf<RoomAmenities>().apply {
                    if (checkBoxAirConditioning.isChecked) add(RoomAmenities.AIR_CONDITIONING)
                    if (checkBoxHeating.isChecked) add(RoomAmenities.HEATING)
                },
                // Additional fields as needed
                roomId = "", // You can generate a unique ID or leave it empty for Firestore to generate one
                landlordId = "", // Replace with the actual landlord ID
                roomStatus = RoomStatus.AVAILABLE, // Set default status or adjust as needed
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                roomImages = emptyList(), // Add images if needed
                likedByUsers = emptyList(), // Initialize empty lists
                dislikedByUsers = emptyList(),
                matches = emptyList()
            )

            // Call the ViewModel function to create the room
            viewModel.createRoom(room)
        }

        backButton.setOnClickListener {
            findNavController().navigateUp() // Use navigateUp instead of popBackStack
        }
    }

}
