package pt.ipca.roomies.ui.main.profile

import pt.ipca.roomies.data.entities.UserProfile
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.SelectedTag

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?>
        get() = _userProfile


    private suspend fun fetchUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = db.collection("userProfiles")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val userProfile = snapshot.documents[0].toObject(UserProfile::class.java)
                Log.d("ProfileViewModel", "Fetched user profile: $userProfile")
                userProfile?.copy(userId = userId)
            } else {
                Log.d("ProfileViewModel", "User profile not found for userId: $userId")

                null
            }
        } catch (e: Exception) {
            // Handle exception
            Log.e("ProfileViewModel", "Error fetching user profile: ${e.message}")
            null
        }
    }

    fun getUserProfile() {
        // Get the current user ID from Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the user is authenticated
        if (userId != null) {
            // Use viewModelScope.launch to perform asynchronous operations
            viewModelScope.launch {
                Log.d("ProfileViewModel", "Fetching user profile for userId: $userId")
                try {
                    // Replace this with your actual logic to fetch the user profile
                    val userProfile = fetchUserProfile(userId)

                    // Only update _userProfile if userProfile is not null
                    userProfile?.let {
                        _userProfile.postValue(userProfile)
                    } ?: run {
                        // Handle the case when the user profile is not found
                        Log.e("ProfileViewModel", "User profile not found")
                    }
                } catch (e: Exception) {
                    // Handle error, log, or report to analytics
                    e.printStackTrace()
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