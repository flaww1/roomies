package pt.ipca.roomies.ui.main.users.room

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserProfileDao
import pt.ipca.roomies.data.entities.*
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory
import pt.ipca.roomies.data.repositories.RoomRepository
import pt.ipca.roomies.data.repositories.RoomViewModelFactory
import pt.ipca.roomies.databinding.FragmentCreateRoomBinding
import pt.ipca.roomies.ui.main.users.habitation.HabitationViewModel
import com.google.firebase.auth.FirebaseAuth

class CreateRoomFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var habitationSpinner: Spinner
    private lateinit var habitationAdapter: ArrayAdapter<Habitation>

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomDao: RoomDao
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var roomRepository: RoomRepository
    private lateinit var habitationDao: HabitationDao

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var habitationViewModel: HabitationViewModel
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

        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
        roomRepository = RoomRepository(userProfileDao, roomDao)
        habitationDao = AppDatabase.getDatabase(requireContext()).habitationDao()

        // Initialize the view models here
        roomViewModel = RoomViewModelFactory(roomRepository).create(RoomViewModel::class.java)
        habitationViewModel = HabitationViewModelFactory(habitationDao)
            .create(HabitationViewModel::class.java)

        habitationSpinner = binding.spinnerHabitation
        habitationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )

        habitationSpinner.adapter = habitationAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        setupViews()
        setupListeners()
        habitationAdapter = HabitationSpinnerAdapter()
        habitationSpinner.adapter = habitationAdapter

        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Observe habitations using LiveData
            habitationViewModel.habitationsByUserId.observe(viewLifecycleOwner) { habitations: List<Habitation> ->
                habitationAdapter.clear()
                habitationAdapter.addAll(habitations)
                habitationAdapter.notifyDataSetChanged()
            }

            // Fetch habitations using userId
            habitationViewModel.getHabitationsByUserId(userId)

        } else {
            // Handle no user logged in
            Log.d("CreateRoomFragment", "No user logged in")
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()

        }


        // Observe the selected room LiveData
        roomViewModel.selectedRoom.observe(viewLifecycleOwner) { room ->
            // Update UI or perform actions based on the selected room, if needed
            Log.d("CreateRoomFragment", "Observed selectedRoom: $room")
        }


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

    private fun setupHabitationObserver() {
        habitationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedHabitation = habitationAdapter.getItem(position)
                if (selectedHabitation != null) {
                    habitationViewModel.setSelectedHabitation(selectedHabitation)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
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
        val habitationId = habitationViewModel.selectedHabitation.value?.habitationId

        val existingRoom = roomViewModel.selectedRoom.value

        Log.d("CreateRoomFragment", "Habitation ID: $habitationId")

        if (habitationId.isNullOrBlank()) {
            showToast("No habitation selected")
            findNavController().navigateUp()
            return
        }

        if (existingRoom == null) {
            // Create a new room
            val room = createRoomObject(habitationId)
            uploadImages(selectedImages) { imageUrls ->
                room.roomImages = imageUrls
                roomViewModel.createRoom(room)
            }
        } else {
            // Update the existing room
            val room = updateRoomObject(habitationId, existingRoom)
            uploadImages(selectedImages) { imageUrls ->
                room.roomImages = imageUrls
                roomViewModel.updateRoom(room)
            }
        }

        findNavController().navigateUp()
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
        if (editTextDescription.text.isEmpty()) {
            showToast("Description cannot be empty.")
            return false
        }
        if (editTextPrice.text.isEmpty()) {
            showToast("Price cannot be empty.")
            return false
        }
        if (selectedImages.isEmpty()) {
            showToast("Please select at least one image.")
            return false
        }
        return true
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

    private inner class HabitationSpinnerAdapter : ArrayAdapter<Habitation>(
        requireContext(),
        android.R.layout.simple_spinner_dropdown_item,
        mutableListOf()
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createView(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createView(position, convertView, parent)
        }

        private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                android.R.layout.simple_spinner_item,
                parent,
                false
            )

            val habitation = getItem(position)
            habitation?.let {
                view.findViewById<TextView>(android.R.id.text1).text = it.address
            }

            return view
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
