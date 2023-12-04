package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import User
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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
import pt.ipca.roomies.data.repositories.ProfileTagsRepository
import pt.ipca.roomies.databinding.FragmentRegistrationUserInterestsBinding
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel

class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var viewModelRegistration: RegistrationViewModel
    private lateinit var profileTagsRepository: ProfileTagsRepository
    private lateinit var selectedTagsByType: MutableMap<TagType, MutableLiveData<MutableSet<ProfileTags>>>
    private val areTagsSelected: MutableMap<TagType, Boolean> = mutableMapOf()
    private val selectedTags: MutableSet<ProfileTags> = mutableSetOf()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userId: String? = null // Use a nullable type

    private lateinit var adapterInterests: TagsAdapter
    private lateinit var adapterLanguages: TagsAdapter
    private lateinit var adapterPersonality: TagsAdapter

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
        selectedTagsByType = mutableMapOf<TagType, MutableLiveData<MutableSet<ProfileTags>>>().also { map ->
            TagType.values().forEach { tagType ->
                map[tagType] = MutableLiveData(mutableSetOf())
            }
        }

        // Initialize Adapter here
        val onTagClickListener: (ProfileTags, Boolean) -> Unit = { tag, isSelected ->
            val selectedTagsForType = selectedTagsByType[tag.tagType]?.value ?: mutableSetOf()
            if (isSelected && !selectedTagsForType.contains(tag)) {
                selectedTagsForType.add(tag)
            } else if (!isSelected && selectedTagsForType.contains(tag)) {
                selectedTagsForType.remove(tag)
            }
            selectedTagsByType[tag.tagType]?.value = selectedTagsForType
            viewModel.updateSelectedTags(tag.tagType, tag, isSelected)
            if (selectedTagsForType.isEmpty()) {
                areTagsSelected[tag.tagType] = false
            }
        }
        // Initialize Adapter here
        adapterInterests = TagsAdapter(
            emptyList(),
            onTagClickListener,
            profileTagsRepository,
            userId ?: "",
            selectedTagsByType = selectedTagsByType,
            selectedTags = selectedTags
        )

        binding.recyclerViewInterests.adapter = adapterInterests

        adapterLanguages = TagsAdapter(
            emptyList(),
            onTagClickListener,
            profileTagsRepository,
            userId ?: "",
            selectedTagsByType = selectedTagsByType,
            selectedTags = selectedTags
        )

        binding.recyclerViewLanguages.adapter = adapterLanguages

        // Initialize Adapter for Personality
        adapterPersonality = TagsAdapter(
            emptyList(),
            onTagClickListener,
            profileTagsRepository,
            userId ?: "",
            selectedTagsByType = selectedTagsByType,
            selectedTags = selectedTags
        )

        binding.recyclerViewPersonality.adapter = adapterPersonality

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelRegistration.user.observe(viewLifecycleOwner) { user ->
            userId = user?.userId ?: ""

            if (userId.isNullOrEmpty()) {
                Log.e("InterestsFragment", "User ID is empty or null")
                Toast.makeText(requireContext(), "User ID is empty or null", Toast.LENGTH_SHORT).show()
            } else {
                adapterInterests.updateUserId(userId ?: "")
                adapterLanguages.updateUserId(userId ?: "")
                adapterPersonality.updateUserId(userId ?: "")
            }
        }

        if (::adapterInterests.isInitialized && ::adapterLanguages.isInitialized && ::adapterPersonality.isInitialized) {
            binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())

            viewModel.availableTagsMap[TagType.Interest]?.observe(viewLifecycleOwner) { availableInterestTags ->
                Log.d("InterestsFragment", "Available Interest Tags Changed: $availableInterestTags")
                adapterInterests.updateData(viewModel.availableTagsMap[TagType.Interest]?.value ?: emptyList(), TagType.Interest)
            }

            viewModel.availableTagsMap[TagType.Language]?.observe(viewLifecycleOwner) { availableLanguageTags ->
                Log.d("InterestsFragment", "Available Language Tags Changed: $availableLanguageTags")
                adapterLanguages.updateData(viewModel.availableTagsMap[TagType.Language]?.value ?: emptyList(), TagType.Language)
            }

            viewModel.availableTagsMap[TagType.Personality]?.observe(viewLifecycleOwner) { availablePersonalityTags ->
                Log.d("InterestsFragment", "Available Personality Tags Changed: $availablePersonalityTags")
                adapterPersonality.updateData(viewModel.availableTagsMap[TagType.Personality]?.value ?: emptyList(), TagType.Personality)
            }

            selectedTagsByType.forEach { (tagType, selectedTagsLiveData) ->
                selectedTagsLiveData.observe(viewLifecycleOwner) { selectedTags ->
                    val areTagsSelected = selectedTags.isNotEmpty()
                    Log.d("InterestsFragment", "TagType: $tagType, AreTagsSelected: $areTagsSelected")
                   // when (tagType) {
                     //   TagType.Interest -> binding.interestTagsSelected = areTagsSelected
                       // TagType.Language -> binding.languageTagsSelected = areTagsSelected
                       // TagType.Personality -> binding.personalityTagsSelected = areTagsSelected
                    //}

              //      binding.nextButton.isEnabled =
                //        binding.interestTagsSelected == true &&
                  //              binding.languageTagsSelected == true &&
                    //            binding.personalityTagsSelected == true
                    Log.d("InterestsFragment", "Is Next Button Enabled: ${binding.nextButton.isEnabled}")
                }
            }


        } else {
            Log.e("InterestsFragment", "Adapters are not initialized")
        }

        binding.nextButton.setOnClickListener {
            val user = viewModelRegistration.user.value
            val selectedInterests = viewModel.selectedTagsMap[TagType.Interest]?.value
            val selectedLanguages = viewModel.selectedTagsMap[TagType.Language]?.value
            val selectedPersonality = viewModel.selectedTagsMap[TagType.Personality]?.value

            if (user != null && !selectedInterests.isNullOrEmpty() && !selectedLanguages.isNullOrEmpty() && !selectedPersonality.isNullOrEmpty()) {
                // Combine all selected tags into a single list
                val allSelectedTags = mutableListOf<UserTags>()

                // Map and add selected interests
                // allSelectedTags.addAll(selectedInterests)
                // Map and add selected languages
                // allSelectedTags.addAll(selectedLanguages)
                // Map and add selected personality
                // allSelectedTags.addAll(selectedPersonality)

                // Update the ViewModel with all selected tags
                viewModel.updateAllSelectedTags(allSelectedTags)

                // Save user and selected tags to Firestore
                lifecycleScope.launch {
                    onFinalStepCompleted(user, allSelectedTags)
                }
            } else {
                // Handle the case when selected tags are null or empty
                Toast.makeText(requireContext(), "Selected tags are null or empty", Toast.LENGTH_SHORT).show()
            }
        }


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun navigateToHomePage() {
        // Use NavController to navigate to the next fragment
        //findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    private suspend fun onFinalStepCompleted(user: User, selectedTags: List<UserTags>) {
        // Save selected tags to Firestore
        selectedTags.forEach { tag ->
            profileTagsRepository.associateTagWithUser(user.userId, tag.tagId, tag.tagType, tag.isSelected)
        }
        // Upload profile picture to Firebase Storage
        viewModelRegistration.selectedImageUri.value?.let { uploadProfilePicture(user.userId, it) }

        // Call registerUser from RegistrationViewModel to complete the registration
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
}



