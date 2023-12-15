package pt.ipca.roomies.ui.main.landlord.room

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.*
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class CreateRoomFragment : Fragment() {

    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerRoomType: Spinner
    private lateinit var spinnerRoomSize: Spinner
    private lateinit var spinnerLeaseDuration: Spinner
    private lateinit var checkBoxAirConditioning: CheckBox
    private lateinit var checkBoxHeating: CheckBox
    private lateinit var createRoomButton: Button
    private lateinit var backButton: Button
    private lateinit var btnSelectImages: Button
    private lateinit var selectedImages: MutableList<Uri>
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var habitationViewModel: HabitationViewModel
    private val MAX_IMAGES = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        habitationViewModel = ViewModelProvider(requireActivity())[HabitationViewModel::class.java]
        roomViewModel = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        initializeViews(view)
        setupViews()
        setupListeners()
    }

    private fun initializeViews(view: View) {
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextPrice = view.findViewById(R.id.editTextPrice)
        spinnerRoomType = view.findViewById(R.id.spinnerRoomType)
        spinnerRoomSize = view.findViewById(R.id.spinnerRoomSize)
        spinnerLeaseDuration = view.findViewById(R.id.spinnerLeaseDuration)
        checkBoxAirConditioning = view.findViewById(R.id.checkBoxAirConditioning)
        checkBoxHeating = view.findViewById(R.id.checkBoxHeating)
        createRoomButton = view.findViewById(R.id.createRoomButton)
        backButton = view.findViewById(R.id.backButton)
        btnSelectImages = view.findViewById(R.id.btnSelectImages)
        selectedImages = mutableListOf()
    }

    private fun setupViews() {
        setupSpinners()
        setupHabitationObserver()
        setupInputValidation()
    }

    private fun setupSpinners() {
        spinnerRoomType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            RoomType.values()
        )

        spinnerRoomSize.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            RoomSize.values()
        )

        spinnerLeaseDuration.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            LeaseDuration.values()
        )
    }

    private fun setupHabitationObserver() {
        habitationViewModel.selectedHabitation.observe(viewLifecycleOwner) { habitation ->
            if (habitation == null) {
                showToast("No habitation selected")
                findNavController().navigateUp()
            }
        }
    }

    private fun setupInputValidation() {
        editTextDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editTextDescription.text.isEmpty()) {
                editTextDescription.error = "Description cannot be empty"
            }
        }

        editTextPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editTextPrice.text.isEmpty()) {
                editTextPrice.error = "Price cannot be empty"
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        btnSelectImages.setOnClickListener {
            openFilePicker()
        }

        createRoomButton.setOnClickListener {
            createOrUpdateRoom()
        }
    }

    private fun createOrUpdateRoom() {
        val habitation = habitationViewModel.selectedHabitation.value
        if (habitation == null || habitation.habitationId?.isBlank() != false) {
            showToast("Invalid habitation ID")
            return
        }

        Log.d("CreateRoomFragment", "Creating a room for habitation: ${habitation.habitationId}")

        val room = if (roomViewModel.selectedRoom.value != null) {
            // If selectedRoom is not null, it means we are updating an existing room
            updateRoomObject(habitation.habitationId!!, roomViewModel.selectedRoom.value!!)
        } else {
            // If selectedRoom is null, it means we are creating a new room
            createRoomObject(habitation.habitationId!!)
        }

        // Create the room in Firestore and get the DocumentReference
        roomViewModel.createRoom(room)
        findNavController().navigateUp()
    }



    private fun createRoomObject(habitationId: String): Room {
        return Room(
            habitationId = habitationId,
            description = editTextDescription.text.toString(),
            price = editTextPrice.text.toString().toDouble(),
            roomType = spinnerRoomType.selectedItem as RoomType,
            roomSize = spinnerRoomSize.selectedItem as RoomSize,
            leaseDuration = spinnerLeaseDuration.selectedItem as LeaseDuration,
            roomAmenities = getSelectedAmenities(),
            roomStatus = RoomStatus.AVAILABLE,
            roomImages = emptyList()
        )
    }


    private fun updateRoomObject(habitationId: String, existingRoom: Room): Room {
        return Room(
            roomId = existingRoom.roomId,
            habitationId = habitationId,
            description = editTextDescription.text.toString(),
            price = editTextPrice.text.toString().toDouble(),
            roomType = spinnerRoomType.selectedItem as RoomType,
            roomSize = spinnerRoomSize.selectedItem as RoomSize,
            leaseDuration = spinnerLeaseDuration.selectedItem as LeaseDuration,
            roomAmenities = getSelectedAmenities(),
            roomStatus = RoomStatus.AVAILABLE,
            roomImages = existingRoom.roomImages
        )
    }

    private fun getSelectedAmenities(): List<RoomAmenities> {
        val amenities = mutableListOf<RoomAmenities>()
        if (checkBoxAirConditioning.isChecked) amenities.add(RoomAmenities.AIR_CONDITIONING)
        if (checkBoxHeating.isChecked) amenities.add(RoomAmenities.HEATING)
        return amenities
    }

    private fun openFilePicker() {
        if (selectedImages.size < MAX_IMAGES) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        } else {
            showToast("Maximum $MAX_IMAGES images allowed.")
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    selectedImages.add(imageUri)
                    updateSelectImagesButton()
                }
            }
        }

    private fun updateSelectImagesButton() {
        btnSelectImages.text = "Select Images (${selectedImages.size}/$MAX_IMAGES)"
        updateCreateRoomButtonState()
    }

    private fun updateCreateRoomButtonState() {
        createRoomButton.isEnabled =
            selectedImages.size in 1..MAX_IMAGES && validateRoomInputs()
    }

    private fun validateRoomInputs(): Boolean {
        return editTextDescription.text.isNotEmpty() &&
                editTextPrice.text.isNotEmpty() &&
                selectedImages.isNotEmpty()
    }

    private fun uploadImages(images: List<Uri>, onComplete: (List<String>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val imageUrls = mutableListOf<String>()
        val uploadTasks = mutableListOf<UploadTask>()

        images.forEach { imageUri ->
            val imageRef = storageRef.child("images/${imageUri.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri)
            uploadTasks.add(uploadTask)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                downloadUrlTask.addOnSuccessListener { uri ->
                    imageUrls.add(uri.toString())
                    if (imageUrls.size == images.size) {
                        onComplete(imageUrls)
                    }
                }
            }
        }
    }
}