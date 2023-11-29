package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import ProfileTagsRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

class RegistrationUserInterestsViewModel : ViewModel() {

    private val _selectedInterestTags: MutableLiveData<List<ProfileTags>> = MutableLiveData()
    private val selectedInterestTags: LiveData<List<ProfileTags>> get() = _selectedInterestTags

    private val profileTagsRepository = ProfileTagsRepository()

    private val availableInterestTags = MutableLiveData<List<ProfileTags>>()
    fun getAvailableInterestTags(): LiveData<List<ProfileTags>> = availableInterestTags

    fun fetchAvailableInterestTags() {
        // Assuming ProfileTagsRepository has a function to get all interest tags
        profileTagsRepository.getTagsByType(
            TagType.Interest,
            onSuccess = { tags ->
                availableInterestTags.value = tags
            },
            onFailure = { e ->
                // Handle failure, e.g., show an error message
                println("Failed to fetch available interest tags: $e")
            }
        )
    }

    fun fetchSelectedInterestTags(): LiveData<List<ProfileTags>> {
        return selectedInterestTags
    }

    fun updateSelectedInterestTags(tag: ProfileTags, isSelected: Boolean) {
        // Get the current list of selected interest tags
        val currentTags = _selectedInterestTags.value ?: emptyList()

        // Check if the tag is already selected
        val isAlreadySelected = currentTags.contains(tag)

        // Create a new list for the updated selected interest tags
        val updatedTags = if (isAlreadySelected) {
            // If the tag is already selected, deselect it
            currentTags - tag
        } else {
            // If the tag is not selected, select it
            currentTags + tag
        }

        // Set the value of the selectedInterestTags LiveData to the updated list
        _selectedInterestTags.value = updatedTags

        // Update the association with the user
        val userId = "your_user_id" // replace with your actual user ID
        val tagId = tag.tagId
        val tagType = TagType.Interest // replace INTEREST with the actual tagType

        profileTagsRepository.associateTagWithUser(userId, tagId, tagType, isSelected)
    }

    private fun convertUserTagsToProfileTags(
        userTags: List<UserTags>,
        profileTags: List<ProfileTags>
    ): List<ProfileTags> {
        val profileTagMap = profileTags.associateBy { it.tagId }
        return userTags.mapNotNull { userTag ->
            profileTagMap[userTag.tagId]?.let { profileTag ->
                // Copy the profile tag and add any additional information from the user tag if needed
                profileTag.copy(tagName = profileTag.tagName)
            }
        }
    }

    fun updateProfileTags(userTags: List<UserTags>) {
        val profileTags = getAvailableInterestTags().value ?: emptyList()
        val updatedProfileTags = convertUserTagsToProfileTags(userTags, profileTags)
        _selectedInterestTags.value = updatedProfileTags
    }


}
