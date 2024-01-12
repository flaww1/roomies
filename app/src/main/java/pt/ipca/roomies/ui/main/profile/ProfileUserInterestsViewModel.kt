package pt.ipca.roomies.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.repositories.ProfileTagRepository

class ProfileUserInterestsViewModel(
    private val profileRepository: ProfileTagRepository,
    private val profileTagRepository: ProfileTagRepository
) : ViewModel() {

    private val selectedTagsByType = mutableMapOf<TagType, MutableLiveData<MutableSet<UserTags>>>()
    val availableTagsMap = mutableMapOf<TagType, MutableLiveData<List<UserTags>>>()
    private val _selectedTags = mutableMapOf<TagType, MutableLiveData<List<UserTags>>>()
    private val areTagsSelected = mutableMapOf<TagType, Boolean>()

    // Expose a LiveData for external access
    val selectedTags: LiveData<Map<TagType, List<UserTags>>>
        get() = MutableLiveData<Map<TagType, List<UserTags>>>().apply {
            _selectedTags.forEach { (tagType, mutableLiveData) ->
                mutableLiveData.observeForever {
                    value = _selectedTags.mapValues { entry ->
                        entry.value.value ?: emptyList()
                    }
                }
            }
        }

    init {
        TagType.values().forEach { tagType ->
            selectedTagsByType[tagType] = MutableLiveData()
            availableTagsMap[tagType] = MutableLiveData()
            _selectedTags[tagType] = MutableLiveData()
            fetchAvailableTagsByType(tagType)
        }
    }

    private fun fetchAvailableTagsByType(tagType: TagType) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val userTagsList = profileRepository.getSelectedTags(userId)

                availableTagsMap[tagType]?.value = userTagsList
            } catch (e: Exception) {
                // Handle failure
                println("Failed to fetch available tags of type $tagType: $e")
            }

            // Rest of the code remains unchanged
        }
    }


    fun updateAllSelectedTags(allSelectedTags: List<UserTags>) {
        allSelectedTags.forEach { tag ->
            val tagType = tag.tagType
            val currentTags = selectedTagsByType[tagType]?.value ?: mutableSetOf()

            val updatedTags = if (tag.isSelected) {
                currentTags + tag
            } else {
                currentTags - tag
            }

            selectedTagsByType[tagType]?.value = updatedTags.toMutableSet()
        }
    }

    fun updateAreTagsSelected(tagType: TagType) {
        val selectedTags = selectedTagsByType[tagType]?.value ?: emptySet()
        areTagsSelected[tagType] = selectedTags.isNotEmpty()
    }

    fun updateSelectedTags(
        tagType: TagType,
        tag: UserTags,
        isSelected: Boolean
    ) {
        viewModelScope.launch {
            val currentSelectedTags = selectedTagsByType[tagType]?.value ?: mutableSetOf()

            if (isSelected) {
                currentSelectedTags.add(tag)
            } else {
                currentSelectedTags.remove(tag)
            }

            selectedTagsByType[tagType]?.value = currentSelectedTags

            areTagsSelected[tagType] = currentSelectedTags.isNotEmpty()

            _selectedTags[tagType]?.value = currentSelectedTags.toList()
        }
    }

    fun getSelectedTags(tagType: TagType): List<UserTags> {
        return selectedTagsByType[tagType]?.value?.toList() ?: emptyList()
    }


}
