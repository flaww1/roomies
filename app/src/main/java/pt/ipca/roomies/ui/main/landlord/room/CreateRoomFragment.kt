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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.LeaseDuration
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.RoomAmenities
import pt.ipca.roomies.data.entities.RoomSize
import pt.ipca.roomies.data.entities.RoomStatus
import pt.ipca.roomies.data.entities.RoomType
import pt.ipca.roomies.ui.main.landlord.SharedHabitationViewModel

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
    private val MAX_IMAGES = 5
    private val sharedHabitationViewModel: SharedHabitationViewModel by activityViewModels()
    private val viewModel: RoomViewModel by viewModels()  // Initialize RoomViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
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

        sharedHabitationViewModel.selectedHabitation.observe(viewLifecycleOwner, Observer { habitation ->
            if (habitation == null) {
                Toast.makeText(requireContext(), "No habitation selected", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
                return@Observer
            }
        })

        editTextDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editTextDescription.text.isEmpty()) {
                    editTextDescription.error = "Description cannot be empty"
                }
            }
        }

        editTextPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editTextPrice.text.isEmpty()) {
                    editTextPrice.error = "Price cannot be empty"
                }
            }
        }

    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        btnSelectImages.setOnClickListener {
            openFilePicker()
        }

        createRoomButton.setOnClickListener {
            createRoom()
        }
    }

    private fun createRoom() {
        val habitationId = sharedHabitationViewModel.selectedHabitation.value?.habitationId
        if (habitationId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Invalid habitation ID", Toast.LENGTH_SHORT).show()
            return
        }

        val room = Room(
            habitationId = habitationId,
            description = editTextDescription.text.toString(),
            price = editTextPrice.text.toString().toDouble(),
            roomType = spinnerRoomType.selectedItem as RoomType,
            roomSize = spinnerRoomSize.selectedItem as RoomSize,
            leaseDuration = spinnerLeaseDuration.selectedItem as LeaseDuration,
            roomAmenities = mutableListOf<RoomAmenities>().apply {
                if (checkBoxAirConditioning.isChecked) add(RoomAmenities.AIR_CONDITIONING)
                if (checkBoxHeating.isChecked) add(RoomAmenities.HEATING)
            },
            roomStatus = RoomStatus.AVAILABLE,
            roomImages = emptyList()
        )

        if (selectedImages.isNotEmpty()) {
            uploadImages(selectedImages.take(MAX_IMAGES)) { imageUrls ->
                room.roomImages = imageUrls
                viewModel.createRoom("habitations/$habitationId/rooms", room)
            }
        } else {
            viewModel.createRoom("habitations/$habitationId/rooms", room)
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

    private fun openFilePicker() {
        if (selectedImages.size < MAX_IMAGES) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        } else {
            Toast.makeText(
                requireContext(),
                "Maximum $MAX_IMAGES images allowed.",
                Toast.LENGTH_SHORT
            ).show()
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
