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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.*
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory
import pt.ipca.roomies.data.repositories.RoomRepository
import pt.ipca.roomies.data.repositories.RoomViewModelFactory
import pt.ipca.roomies.databinding.FragmentCreateRoomBinding
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class CreateRoomFragment : Fragment() {
    private lateinit var roomRecyclerView: RecyclerView
    private var roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
    private val userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
    private val roomRepository = RoomRepository(userProfileDao, roomDao)
    private val habitationDao = AppDatabase.getDatabase(requireContext()).habitationDao()

    private val roomViewModel: RoomViewModel by viewModels {
        RoomViewModelFactory(roomRepository)
    }

    private val habitationViewModel: HabitationViewModel by viewModels {
        HabitationViewModelFactory(habitationDao)
    }
    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!

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
    private val MAX_IMAGES = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        setupViews()
        setupListeners()
    }

    private fun initializeViews() {
        editTextDescription = binding.editTextDescription
        editTextPrice = binding.editTextPrice
        spinnerRoomType = binding.spinnerRoomType
        spinnerRoomSize = binding.spinnerRoomSize
        spinnerLeaseDuration = binding.spinnerLeaseDuration
        checkBoxAirConditioning = binding.checkBoxAirConditioning
        checkBoxHeating = binding.checkBoxHeating
        createRoomButton = binding.createRoomButton
        backButton = binding.backButton
        btnSelectImages = binding.btnSelectImages
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
            RoomType.entries.toTypedArray()
        )

        spinnerRoomSize.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            RoomSize.entries.toTypedArray()
        )

        spinnerLeaseDuration.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            LeaseDuration.entries.toTypedArray()
        )
    }

    // Inside CreateRoomFragment
    private fun setupHabitationObserver() {
        habitationViewModel.selectedHabitation.observe(viewLifecycleOwner) { habitation ->
            // Use habitation.habitationId to create a room
            // Make sure habitation is not null and habitationId is not blank
            if (habitation == null || habitation.habitationId.isBlank()) {
                showToast("No habitation selected")
                findNavController().navigateUp()
            }
        }
    }


    private fun setupInputValidation() {
        editTextDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editTextDescription.text.isEmpty()) {
                editTextDescription.error = getString(R.string.error_description_empty)
            }
        }

        editTextPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editTextPrice.text.isEmpty()) {
                editTextPrice.error = getString(R.string.error_price_empty)
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
        if (habitation == null || habitation.habitationId.isBlank()) {
            showToast(getString(R.string.invalid_habitation_id))
            return
        }

        // Use habitation.habitationId to create or update the room
        val room = createRoomObject(habitation.habitationId)
        roomViewModel.createRoom(room)
        // Navigate back or update UI accordingly
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
            showToast(getString(R.string.max_images_limit, MAX_IMAGES))
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
        btnSelectImages.text = getString(R.string.select_images_count, selectedImages.size, MAX_IMAGES)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
