// FragmentCreateRoom.kt
package pt.ipca.roomies.ui.main.landlord.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.LeaseDuration
import pt.ipca.roomies.data.entities.RoomAmenities
import pt.ipca.roomies.data.entities.RoomSize
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
        // Set up your spinner adapters here
        // For example:
        // val roomTypeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.room_types, android.R.layout.simple_spinner_item)
        // roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // spinnerRoomType.adapter = roomTypeAdapter
    }

    private fun setupListeners() {
        createRoomButton.setOnClickListener {
            // Handle the logic for creating a room
            // Retrieve values from views and create a Room object
            // Example: viewModel.createRoom(room)
        }

        backButton.setOnClickListener {
            // Handle navigation back to the main screen
        }
    }
}
