package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import InterestTagsAdapter
import ProfileTagsRepository
import RegistrationViewModel
import UserProfile
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
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var viewModelRegistration: RegistrationViewModel
    private lateinit var adapter: InterestTagsAdapter
    private lateinit var profileTagsRepository: ProfileTagsRepository

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("InterestsFragment", "onCreateView")

        binding = FragmentRegistrationUserInterestsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegistrationUserInterestsViewModel::class.java]
        viewModelRegistration =
            ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
        profileTagsRepository = ProfileTagsRepository()

        userId = viewModelRegistration._user.value?.userId ?: ""
        Log.d("InterestsFragment", "User ID: $userId")

        // Handle the case when userId is empty or null
        if (userId.isEmpty()) {
            // Log an error or show a toast
            Log.e("InterestsFragment", "User ID is empty or null")
            Toast.makeText(requireContext(), "User ID is empty or null", Toast.LENGTH_SHORT).show()
            return inflater.inflate(R.layout.fragment_error_layout, container, false)
        }


        // Initialize Adapter here
        // Initialize Adapter here
        adapter = InterestTagsAdapter(listOf(), { tag ->
            // Get the current list of selected interest tags
            val currentTags = viewModel.fetchSelectedInterestTags().value ?: emptyList()

            // Determine if the tag is selected
            val isSelected = tag in currentTags

            viewModel.updateSelectedInterestTags(tag, isSelected)
        }, profileTagsRepository, userId)
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
            if (userId.isEmpty()) {
                // Log an error or show a toast
                Log.e("InterestsFragment", "User ID is empty or null")
                Toast.makeText(requireContext(), "User ID is empty or null", Toast.LENGTH_SHORT).show()
                // Return or show an error view
            } else {
                // Initialize Adapter here
                adapter = InterestTagsAdapter(
                    listOf(), // Pass your list of interest tags here
                    { tag ->
                        // Click listener logic
                        // Get the current list of selected interest tags
                        val currentTags = viewModel.fetchSelectedInterestTags().value ?: emptyList()

                        // Determine if the tag is selected
                        val isSelected = tag in currentTags

                        viewModel.updateSelectedInterestTags(tag, isSelected)
                    },
                    profileTagsRepository,
                    userId
                )
                binding.recyclerViewInterests.adapter = adapter
            }
        }
        if (::adapter.isInitialized) {
            binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())

            // Fetch available interest tags and observe the LiveData
            viewModel.fetchAvailableInterestTags()
            viewModel.getAvailableInterestTags().observe(viewLifecycleOwner) { availableInterestTags ->
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
            // Navigate to the UserProfileInfoFragment
            val selectedInterests = viewModel.fetchSelectedInterestTags().value

            if (!selectedInterests.isNullOrEmpty()) {
                // Assuming you have default values or some logic to determine tagType

                // Update isSelected based on the selected tags
                val updatedTags = selectedInterests.map { tag ->
                    // Assuming tagType is determined somewhere in your code or you have a default value
                    val tagType = TagType.INTEREST // replace INTEREST with the actual tagType

                    // Create a UserTags object with the necessary information
                    UserTags(userTagId = null, userId = userId, tagId = tag.tagId, tagType = tagType, isSelected = true)
                }



                viewModel.updateProfileTags(updatedTags)
                navigateToHomePage()
                lifecycleScope.launch {
                    onFinalStepCompleted(updatedTags)
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

    private suspend fun onFinalStepCompleted(selectedTags: List<UserTags>) {
        val viewModel = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
        val user = viewModel.user.value



        if (user != null) {
            // Save selected tags to Firestore
            saveSelectedTagsToFirestore(user.userId, selectedTags)
            viewModel.selectedImageUri.value?.let { uploadProfilePicture(user.userId, it) }

        }
    }

    private fun getCurrentTimestamp(): String {
        // Implement getCurrentTimestamp() accordingly


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
    private suspend fun uploadProfilePicture(userId: String, imageUri: Uri) {
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
    private suspend fun saveSelectedTagsToFirestore(userId: String, selectedTags: List<UserTags>) {
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
            tagsCollection.add(userTag).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Tag saved successfully
                } else {
                    // Failed to save tag
                }
            }
        }
    }

    // This extension function is used to convert UserProfile to a map
    private fun UserProfile.toMap(): Map<String, Any> {
        return mapOf(
            "profilePictureUrl" to profilePictureUrl,
            "location" to location,
            "bio" to bio,
            "gender" to gender,
            "occupation" to occupation
            // Add other profile fields as needed
        )
    }
}
