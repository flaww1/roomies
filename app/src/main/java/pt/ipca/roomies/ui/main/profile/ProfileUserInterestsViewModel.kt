package pt.ipca.roomies.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.repositories.ProfileTagsRepository




class ProfileUserInterestsViewModel : ViewModel() {

    private val profileTagsRepository = ProfileTagsRepository()
    private val selectedTagsByType =
        mutableMapOf<TagType, MutableLiveData<MutableSet<UserTags>>>()
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
            profileTagsRepository.getTagsByType(
                tagType,
                onSuccess = { snapshots ->
                    // Convert DocumentSnapshots to UserTags
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val userTagsList = snapshots.map { snapshot ->
                        //val profileTag = snapshot.documents[0].toObject(ProfileTags::class.java)
                        val documentId = snapshot.tagId

                        //convertToUserTags(profileTag!!, userId, documentId)
                    }
                  //  availableTagsMap[tagType]?.value = userTagsList
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to fetch available tags of type $tagType: $e")
                }
            )
        }

        selectedTagsByType.forEach { (tagType, selectedTagsLiveData) ->
            selectedTagsLiveData.observeForever { selectedTags ->
                val filteredAvailableTags = availableTagsMap[tagType]?.value?.filterNot {
                    it in selectedTags
                } ?: emptyList()
                availableTagsMap[tagType]?.value = filteredAvailableTags
            }
        }
    }



    private fun convertToUserTags(profileTag: ProfileTags, userId: String, documentId: String): UserTags {
        return UserTags(
            userId = userId,
            tagId = documentId,
            tagType = profileTag.tagType,
            isSelected = profileTag.isSelected,
            tagName = profileTag.tagName
        )
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
}
