package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.repositories.ProfileTagsRepository

class RegistrationUserInterestsViewModel : ViewModel() {


    private val profileTagsRepository = ProfileTagsRepository()
    private val _selectedTags: MutableLiveData<Map<TagType, List<ProfileTags>>> = MutableLiveData()

    val selectedTags: LiveData<Map<TagType, List<ProfileTags>>> get() = _selectedTags
    // Create a map to store LiveData for each tag type
    val selectedTagsMap = mutableMapOf<TagType, MutableLiveData<List<ProfileTags>>>()

    // Create a map to store LiveData for each available tag type
    val availableTagsMap = mutableMapOf<TagType, MutableLiveData<List<ProfileTags>>>()
    fun updateSelectedTags(tagType: TagType, tag: ProfileTags, isSelected: Boolean) {
        val currentTags = selectedTagsMap[tagType]?.value ?: emptyList()

        val updatedTags = if (isSelected) {
            currentTags + tag
        } else {
            currentTags - tag
        }

        selectedTagsMap[tagType]?.value = updatedTags
    }

    init {
        // Initialize LiveData for each tag type
        TagType.values().forEach { tagType ->
            selectedTagsMap[tagType] = MutableLiveData()
            availableTagsMap[tagType] = MutableLiveData()
            fetchAvailableTagsByType(tagType)
        }
    }

    private fun fetchAvailableTagsByType(tagType: TagType) {
        viewModelScope.launch {
            profileTagsRepository.getTagsByType(
                tagType,
                onSuccess = { tags ->
                    availableTagsMap[tagType]?.value = tags
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to fetch available tags of type $tagType: $e")
                }
            )
        }
    }

    // Generic function to fetch selected tags for a given tag type
    fun fetchSelectedTags(tagType: TagType): LiveData<List<ProfileTags>> {
        return selectedTagsMap[tagType] ?: MutableLiveData()
    }

    // Generic function to update selected tags for a given tag type

    private fun convertUserTagsToProfileTags(
        userTags: List<UserTags>,
        profileTags: List<ProfileTags>
    ): List<ProfileTags> {
        val profileTagMap = profileTags.associateBy { it.tagId }
        return userTags.mapNotNull { userTag ->
            profileTagMap[userTag.tagId]?.let { profileTag ->
                profileTag.copy(tagName = profileTag.tagName)
            }
        }
    }
    fun updateProfileTags(userTags: List<UserTags>, tagType: TagType) {
        val profileTags = availableTagsMap[tagType]?.value ?: emptyList()
        val updatedProfileTags = convertUserTagsToProfileTags(userTags, profileTags)
        selectedTagsMap[tagType]?.value = updatedProfileTags
    }

    // Add a function to update all selected tags
    fun updateAllSelectedTags(allSelectedTags: List<UserTags>) {
        allSelectedTags.forEach { tag ->
            val tagType = tag.tagType
            val currentTags = selectedTagsMap[tagType]?.value ?: emptyList()
            val updatedTags = if (tag.isSelected) {
                currentTags + tag
            } else {
                currentTags - tag
            }
            //selectedTagsMap[tagType]?.value = updatedTags
        }
    }
}










