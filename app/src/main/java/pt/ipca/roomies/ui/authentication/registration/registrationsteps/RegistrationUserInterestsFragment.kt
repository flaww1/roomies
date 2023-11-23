package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import InterestTagsAdapter
import ProfileTagsRepository
import RegistrationViewModel
import UserProfile
import pt.ipca.roomies.databinding.FragmentRegistrationUserInterestsBinding
import android.os.Bundle
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.UserTags

class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var viewModelRegistration: RegistrationViewModel
    private lateinit var adapter: InterestTagsAdapter

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = FragmentRegistrationUserInterestsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegistrationUserInterestsViewModel::class.java]
        lateinit var profileTagsRepository: ProfileTagsRepository

        viewModelRegistration =
            ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]

        val user = viewModelRegistration._user.value
        val userId = user?.userId

        // Check if userId is not null
        if (userId != null) {
            // Initialize Adapter here
            adapter = InterestTagsAdapter(listOf(), { tag ->
                // Get the current list of selected interest tags
                val currentTags = viewModel.getSelectedInterestTags().value ?: emptyList()

                // Determine if the tag is selected
                val isSelected = tag in currentTags

                viewModel.updateSelectedInterestTags(
                    tag,
                    isSelected
                )
            }, profileTagsRepository, userId)

            binding.recyclerViewInterests.adapter = adapter
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())
        // Implement UI interactions
        binding.nextButton.setOnClickListener {
            // Navigate to the UserProfileInfoFragment
            val selectedInterests = viewModel.getSelectedInterestTags().value

            if (selectedInterests != null && selectedInterests.isNotEmpty()) {
                // Assuming you have default values or some logic to determine tagType
                val tagType = "DefaultTagType"

                // Update isSelected based on the selected tags
                val updatedTags = selectedInterests.map { tag ->
                    tag.copy(tagType = tagType, isSelected = true)
                }

                viewModel.updateProfileTags(updatedTags)
                navigateToHomePage()
                onFinalStepCompleted(updatedTags)
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
        viewModel.getSelectedInterestTags().observe(viewLifecycleOwner) {
            // Handle changes, if needed

            // For example, update UI to reflect selected tags
        }

        // Populate the adapter with data
        adapter.updateData(viewModel.getAvailableInterestTags())
    }

    private fun navigateToHomePage() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    private suspend fun onFinalStepCompleted(selectedTags: List<ProfileTags>) {
        val viewModel = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
        val user = viewModel.user.value

        if (user != null) {
            // Save selected tags to Firestore
            saveSelectedTagsToFirestore(user.userId, selectedTags)
        }
    }

    private suspend fun saveSelectedTagsToFirestore(userId: String, selectedTags: List<ProfileTags>) {
        val tagsCollection = firestore.collection("userTags")

        // Save each selected tag to Firestore
        selectedTags.forEach { tag ->
            val userTag = UserTags(userId, tag.tagId, tag.tagType, isSelected = tag.isSelected)
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
            "pronouns" to pronouns,
            "gender" to gender,
            "occupation" to occupation
            // Add other profile fields as needed
        )
    }
}
