package pt.ipca.roomies.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.ProfileRepository
import pt.ipca.roomies.data.repositories.ProfileTagRepository

import pt.ipca.roomies.databinding.FragmentProfileUserInterestsBinding
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel

class ProfileUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentProfileUserInterestsBinding
    private lateinit var viewModel: ProfileUserInterestsViewModel
    private val profileTagsDao = AppDatabase.getDatabase(requireContext()).profileTagsDao()

    private val viewModelRegistration: RegistrationViewModel by lazy {
        ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
    }
    private val profileTagsRepository by lazy {
        ProfileTagRepository(profileTagsDao)
    }
    private var selectedTagsByType =
        mutableMapOf<TagType, MutableLiveData<MutableSet<UserTags>>>()
    private val areTagsSelected: MutableMap<TagType, Boolean> = mutableMapOf()
    private val selectedTags: MutableSet<UserTags> = mutableSetOf()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userId: String? = null // Use a nullable type

    private lateinit var adapterInterests: TagsAdapter
    private lateinit var adapterLanguages: TagsAdapter
    private lateinit var adapterPersonality: TagsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileUserInterestsBinding.inflate(inflater, container, false)


        // Initialize selectedTagsByType before using it
        selectedTagsByType = mutableMapOf<TagType, MutableLiveData<MutableSet<UserTags>>>().also { map ->
            TagType.values().forEach { tagType ->
                map[tagType] = MutableLiveData(mutableSetOf())
            }
        }

        // Initialize Adapter here
        val onTagClickListener: (UserTags, Boolean) -> Unit = { tag, isSelected ->
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

        // Initialize Adapter for Interests
        adapterInterests = TagsAdapter(
            emptyList(),
            onTagClickListener,
            binding.recyclerViewInterests, // Pass the correct RecyclerView instance
            profileTagsRepository,
            userId ?: "",
            selectedTagsByType = selectedTagsByType,
            selectedTags = selectedTags
        )

        binding.recyclerViewInterests.adapter = adapterInterests

        // Initialize Adapter for Languages
        adapterLanguages = TagsAdapter(
            emptyList(),
            onTagClickListener,
            binding.recyclerViewLanguages, // Pass the correct RecyclerView instance
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
            binding.recyclerViewPersonality, // Pass the correct RecyclerView instance
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
                val userTagsList = availableInterestTags?.map { convertToUserTags(it) } ?: emptyList()
                adapterInterests.updateData(userTagsList, TagType.Interest)
            }

            viewModel.availableTagsMap[TagType.Language]?.observe(viewLifecycleOwner) { availableLanguageTags ->
                Log.d("InterestsFragment", "Available Language Tags Changed: $availableLanguageTags")
                val userTagsList = availableLanguageTags?.map { convertToUserTags(it) } ?: emptyList()
                adapterLanguages.updateData(userTagsList, TagType.Language)
            }

            viewModel.availableTagsMap[TagType.Personality]?.observe(viewLifecycleOwner) { availablePersonalityTags ->
                Log.d("InterestsFragment", "Available Personality Tags Changed: $availablePersonalityTags")
                val userTagsList = availablePersonalityTags?.map { convertToUserTags(it) } ?: emptyList()
                adapterPersonality.updateData(userTagsList, TagType.Personality)
            }

            selectedTagsByType.forEach { (tagType, selectedTagsLiveData) ->
                selectedTagsLiveData.observe(viewLifecycleOwner) { selectedTags ->
                    val areTagsSelected = selectedTags.isNotEmpty()
                    Log.d("InterestsFragment", "TagType: $tagType, AreTagsSelected: $areTagsSelected")
                    when (tagType) {
                        TagType.Interest -> binding.interestTagsSelected = areTagsSelected
                        TagType.Language -> binding.languageTagsSelected = areTagsSelected
                        TagType.Personality -> binding.personalityTagsSelected = areTagsSelected
                    }

                    binding.nextButton.isEnabled =
                        binding.interestTagsSelected == true &&
                                binding.languageTagsSelected == true &&
                                binding.personalityTagsSelected == true
                    Log.d("InterestsFragment", "Is Next Button Enabled: ${binding.nextButton.isEnabled}")
                }
            }

        } else {
            Log.e("InterestsFragment", "Adapters are not initialized")
        }

        binding.nextButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                val selectedInterests = selectedTagsByType[TagType.Interest]?.value ?: emptySet()
                val selectedLanguages = selectedTagsByType[TagType.Language]?.value ?: emptySet()
                val selectedPersonality = selectedTagsByType[TagType.Personality]?.value ?: emptySet()

                // Combine all selected tags into a single list
                val allSelectedTags = mutableListOf<UserTags>()

                // Map and add selected interests
                allSelectedTags.addAll(selectedInterests.map { convertToUserTags(it) })
                // Map and add selected languages
                allSelectedTags.addAll(selectedLanguages.map { convertToUserTags(it) })
                // Map and add selected personality
                allSelectedTags.addAll(selectedPersonality.map { convertToUserTags(it) })

                // Update the ViewModel with all selected tags
                viewModel.updateAllSelectedTags(allSelectedTags)

                // Save user and selected tags to Firestore
                lifecycleScope.launch {
                    onFinalStepCompleted(user, allSelectedTags)
                }
            } else {
                // Handle the case when the user is null
                Toast.makeText(requireContext(), "User is null", Toast.LENGTH_SHORT).show()
            }
        }


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun navigateToProfilePage() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_profileUserInterestsFragment_to_profileFragment)
    }

    private suspend fun onFinalStepCompleted(user: FirebaseUser?, selectedTags: List<UserTags>) {
        // Check if the user is not null before proceeding
        if (user != null) {
            // Save selected tags to Firestore
            selectedTags.forEach { tag ->
                profileTagsRepository.associateTagWithUser(user.uid, tag.tagId, tag.tagType, tag.isSelected)
            }

            // Other operations specific to completing the user profile and navigating to the next fragment
            navigateToProfilePage()
        } else {
            // Handle the case when the user is null
            Toast.makeText(requireContext(), "User is null", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertToUserTags(profileTag: UserTags): UserTags {
        return UserTags(
            userId = userId ?: "", // You need to provide the appropriate value for userId
            tagId = profileTag.tagId,
            tagType = profileTag.tagType,
            isSelected = profileTag.isSelected,
            tagName = profileTag.tagName
        )
    }

}
