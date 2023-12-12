package pt.ipca.roomies.ui.main.profile

import UserProfile
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.UserProfileWithTags

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: MutableLiveData<UserProfile?> get() = _userProfile

    // Function to update the user's profile data
    fun updateProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
        // You can also update the profile data in your data repository or backend here
    }

    // Function to check if the user has a profile
    fun hasProfile(): Boolean {
        return userProfile.value != null
    }
    private suspend fun fetchUserProfile(userId: String): UserProfile? {
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
    fun getUserProfile() {
        // Get the current user ID from Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the user is authenticated
        if (userId != null) {
            // Assuming you have the userId available, fetch the user profile
            viewModelScope.launch {
                val userProfile = fetchUserProfile(userId)

                // Only update _userProfile if userProfile is not null
                userProfile?.let {
                    _userProfile.value = userProfile
                } ?: run {
                    // Handle the case when the user profile is not found
                    Log.e("ProfileViewModel", "User profile not found")
                }
            }
        } else {
            // Handle the case when the user is not authenticated
            // You might want to show an error message or navigate to the login screen
            Log.e("ProfileViewModel", "User not authenticated")
        }
    }


    private suspend fun getSelectedTags(userId: String): List<SelectedTag> {
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