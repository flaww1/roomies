package pt.ipca.roomies.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.UserProfile
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.repositories.ProfileRepository
import pt.ipca.roomies.data.repositories.RoomRepository

class ProfileViewModel(
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?>
        get() = _userProfile

    private val _selectedTags = MutableLiveData<List<SelectedTag>?>()
    val selectedTags: MutableLiveData<List<SelectedTag>?>
        get() = _selectedTags

    fun getUserProfileAndTags() {
        viewModelScope.launch {
            try {
                val userId = roomRepository.getCurrentUserId()

                if (userId != null) {
                    // Attempt to fetch from Room
                    fetchUserProfileFromRoom(userId)

                    // Fetch from Firebase
                    fetchUserProfileFromFirestore(userId)

                    // Fetch selected tags
                    fetchSelectedTags(userId)
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private suspend fun fetchUserProfileFromRoom(userId: String) {
        val localUserProfile = roomRepository.getUserProfileByUserId(userId).value
        _userProfile.postValue(localUserProfile)
    }

    private suspend fun fetchUserProfileFromFirestore(userId: String) {
        try {
            val remoteUserProfile = profileRepository.getUserProfileByUserId(userId).value
            remoteUserProfile?.let {
                // Update Room with Firebase data
                roomRepository.insertOrUpdateUserProfileLocally(it)
                _userProfile.postValue(it)
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }


    private suspend fun fetchSelectedTags(userId: String) {
        try {
            val userTagsList = profileRepository.getSelectedTags(userId)
            val selectedTags = convertToSelectedTags(userId, userTagsList)
            _selectedTags.postValue(selectedTags)
        } catch (e: Exception) {
            // Handle exception
        }
    }

    private fun convertToSelectedTags(userId: String, userTagsList: List<UserTags>): List<SelectedTag>? {
        return userTagsList.map { userTag ->
            SelectedTag(
                userId = userId,  // Pass the userId to the SelectedTag constructor
                tagId = userTag.tagId,
                tagType = userTag.tagType,
                isSelected = userTag.isSelected
            )
        }
    }



    fun getUserProfile() {
        viewModelScope.launch {
            try {
                val userId = roomRepository.getCurrentUserId()

                if (userId != null) {
                    // Attempt to fetch from Room
                    fetchUserProfileFromRoom(userId)

                    // Fetch from Firebase
                    fetchUserProfileFromFirestore(userId)
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
