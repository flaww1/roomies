package pt.ipca.roomies.ui.main.profile

import UserProfile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.UserProfileWithTags

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    // Function to update the user's profile data
    fun updateProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
        // You can also update the profile data in your data repository or backend here
    }

    // Function to check if the user has a profile
    fun hasProfile(): Boolean {
        return _userProfile.value != null
    }

    suspend fun getUserProfileWithTags(userId: String): UserProfileWithTags? {
        return try {
            val userProfile = getUserProfile(userId)
            val selectedTags = getSelectedTags(userId)

            if (userProfile != null) {
                UserProfileWithTags(userProfile, selectedTags)
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle exception
            null
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = db.collection("userProfiles")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val userProfile = snapshot.documents[0].toObject(UserProfile::class.java)
                userProfile
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle exception
            null
        }
    }

    suspend fun getSelectedTags(userId: String): List<SelectedTag> {
        return try {
            val snapshot = db.collection("selectedTags")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val selectedTags = mutableListOf<SelectedTag>()
            for (document in snapshot.documents) {
                val tag = document.toObject(SelectedTag::class.java)
                tag?.let {
                    selectedTags.add(it)
                }
            }

            selectedTags
        } catch (e: Exception) {
            // Handle exception
            emptyList()
        }
    }


}