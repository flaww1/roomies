package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import ProfileTagsRepository
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
import User
import android.net.Uri
import pt.ipca.roomies.databinding.FragmentRegistrationUserInterestsBinding
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var viewModelRegistration: RegistrationViewModel
    private lateinit var adapter: InterestTagsAdapter
    private lateinit var profileTagsRepository: ProfileTagsRepository
    private lateinit var selectedTagsByType: MutableMap<TagType, MutableSet<ProfileTags>>

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userId: String? = null // Use a nullable type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationUserInterestsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegistrationUserInterestsViewModel::class.java]
        viewModelRegistration =
            ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
        profileTagsRepository = ProfileTagsRepository()

        // Initialize selectedTagsByType before using it
        selectedTagsByType = mutableMapOf()
        // Initialize Adapter here
        val onTagClickListener: (ProfileTags) -> Unit = { tag ->
            val currentTags = viewModel.fetchSelectedInterestTags().value ?: emptyList()
            val isSelected = tag in currentTags
            viewModel.updateSelectedInterestTags(tag, isSelected)
            adapter.toggleSelection(tag)
        }

        // Initialize Adapter here
        adapter = InterestTagsAdapter(
            listOf(),
            onTagClickListener, // Pass onTagClickListener here
            profileTagsRepository,
            userId ?: "",
            selectedTagsByType // Pass the selectedTagsByType map to the adapter
        )

        binding.recyclerViewInterests.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("InterestsFragment", "onViewCreated")

        viewModelRegistration.user.observe(viewLifecycleOwner) { user ->
            userId = user?.userId ?: ""
            Log.d("InterestsFragment", "User ID: $userId")

            // Handle the case when userId is empty or null
            if (userId.isNullOrEmpty()) {
                // Log an error or show a toast
                Log.e("InterestsFragment", "User ID is empty or null")
                Toast.makeText(requireContext(), "User ID is empty or null", Toast.LENGTH_SHORT).show()
                // Return or show an error view
            } else {
                // Update the userId in the adapter when it's available
                adapter.updateUserId(userId ?: "")
            }
        }

        // Ensure adapter is initialized
        if (::adapter.isInitialized) {
            // Set the layout manager
            binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())

            // Fetch available interest tags and observe the LiveData
            viewModel.fetchAvailableInterestTags()
            viewModel.getAvailableInterestTags().observe(viewLifecycleOwner) { availableInterestTags ->
                Log.d("InterestsFragment", "Available Interest Tags Changed: $availableInterestTags")

                // Populate the adapter with data
                adapter.updateData(availableInterestTags)
            }
        } else {
            // Log an error or handle the case where the adapter is not initialized
            Log.e("InterestsFragment", "Adapter is not initialized")
        }

        // Rest of your onViewCreated method..

        if (::adapter.isInitialized) {
            binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())

            // Fetch available interest tags and observe the LiveData
            viewModel.fetchAvailableInterestTags()
            viewModel.getAvailableInterestTags().observe(viewLifecycleOwner) { availableInterestTags ->
                Log.d("InterestsFragment", "Available Interest Tags Changed: $availableInterestTags")
                // Populate the adapter with data
                adapter.updateData(availableInterestTags)
            }

            // ... (rest of the onViewCreated method)
        } else {
            // Log an error or handle the case where the adapter is not initialized
            Log.e("InterestsFragment", "Adapter is not initialized")
        }

        binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())

        // Fetch available interest tags and observe the LiveData
        viewModel.fetchAvailableInterestTags()
        viewModel.getAvailableInterestTags().observe(viewLifecycleOwner) { availableInterestTags ->
            // Populate the adapter with data
            adapter.updateData(availableInterestTags)
        }
        // Implement UI interactions
        binding.nextButton.setOnClickListener {
            // Fetch user data from pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
            val user = viewModelRegistration.user.value

            // Fetch selected interest tags from ViewModel
            val selectedInterests = viewModel.fetchSelectedInterestTags().value

            if (user != null && !selectedInterests.isNullOrEmpty()) {
                // Update isSelected based on the selected tags
                val updatedTags = selectedInterests.map { tag ->
                    val tagType = TagType.Interest // replace INTEREST with the actual tagType

                    // Create a UserTags object with the necessary information
                    UserTags(userTagId = null, userId = user.userId, tagId = tag.tagId, tagType = tagType, isSelected = true)
                }

                // Update the user's profile tags in the ViewModel
                viewModel.updateProfileTags(updatedTags)

                // Save user and selected tags to Firestore
                lifecycleScope.launch {
                    onFinalStepCompleted(user, updatedTags)
                }
            } else {
                // Handle the case when selectedInterests is null or empty
                // You may want to show a message or handle it appropriately
            }
        }



        // Implement back button functionality
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }
        // Observe changes in the selected interest tags
        viewModel.fetchSelectedInterestTags().observe(viewLifecycleOwner) {
            // Handle changes, if needed

            // For example, update UI to reflect selected tags
        }

        // Populate the adapter with data
    adapter.updateData(listOf())
    }

    private fun navigateToHomePage() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    // Inside RegistrationUserInterestsFragment

// ...

    // Inside the function where you handle the final step of the registration process
    private suspend fun onFinalStepCompleted(user: User, selectedTags: List<UserTags>) {
        // Save selected tags to Firestore
        saveSelectedTagsToFirestore(user.userId, selectedTags)

        // Upload profile picture to Firebase Storage
        viewModelRegistration.selectedImageUri.value?.let { uploadProfilePicture(user.userId, it) }

        // Call registerUser from pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel to complete the registration
        val registrationResult = viewModelRegistration.registerUser()

        // Check the registration result
        if (registrationResult) {
            // Registration successful, navigate to the next fragment or perform other actions
            navigateToHomePage()
        } else {
            // Registration failed, handle the error
            // You can use viewModelRegistration.errorMessage.value to get the error message
            Toast.makeText(requireContext(), viewModelRegistration.errorMessage.value, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfilePictureUrl(userId: String, profilePictureUrl: String) {
        // Update the user's profile with the new profile picture URL in Firestore
        val userCollection = firestore.collection("users")
        userCollection.document(userId)
            .update("profilePictureUrl", profilePictureUrl)
            .addOnSuccessListener {
                // Profile picture URL updated successfully
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
    private fun getStorageReference(uri: Uri): StorageReference {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        return storageReference.child("profile_pictures/${System.currentTimeMillis()}_${uri.lastPathSegment}")
    }
    private fun uploadProfilePicture(userId: String, imageUri: Uri) {
        // Your image upload logic here
        // Use Firebase Storage or any other method to upload the image

        // Example using Firebase Storage
        val storageReference = getStorageReference(imageUri)
        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Update the user's profile with the download URL
                    updateProfilePictureUrl(userId, downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
    private fun saveSelectedTagsToFirestore(userId: String, selectedTags: List<UserTags>) {
        val tagsCollection = firestore.collection("userTags")

        // Save each selected tag to Firestore
        selectedTags.forEach { tag ->
            val userTag = UserTags(
                userTagId = null,
                userId = userId,
                tagId = tag.tagId,
                tagType = tag.tagType,
                isSelected = true
            )

            // Add the userTag to Firestore

            tagsCollection.add(userTag)
                .addOnSuccessListener {
                    // User tag saved successfully
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

}
