package pt.ipca.roomies.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.dao.UserProfileDao
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.UserProfile
import pt.ipca.roomies.data.entities.UserTags

class ProfileRepository(private val userProfileDao: UserProfileDao) {
    private val db = FirebaseFirestore.getInstance()



    // Local Room database operations

    suspend fun getUserProfileByUserId(userId: String): LiveData<UserProfile?> {
        return userProfileDao.getUserProfileByUserId(userId).map { it?.toUserProfile() }
    }




    // Firestore operations

    suspend fun createUserProfileInFirestore(
        userProfile: UserProfile,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        scope: CoroutineScope
    ) = withContext(Dispatchers.IO) {
        try {
            db.collection("userProfiles")
                .document(userProfile.userId)
                .set(userProfile)
                .addOnSuccessListener {
                    // Update the local database with the Firestore document ID
                    val updatedUserProfile = userProfile.copy(documentId = userProfile.userId)
                    scope.launch {
                        insertOrUpdateUserProfileLocally(updatedUserProfile)
                    }
                    onSuccess.invoke()
                }
                .addOnFailureListener { e ->
                    onFailure.invoke(e)
                }
        } catch (e: Exception) {
            onFailure.invoke(e)
        }
    }


    private suspend fun insertOrUpdateUserProfileLocally(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            userProfileDao.insertUserProfile(userProfile)
        }
    }

    fun updateUserProfileInFirestore(
        documentId: String,
        userProfile: UserProfile,
        onSuccess: () -> Unit,
        scope: CoroutineScope,

        onFailure: (Exception) -> Unit
    ) {
        db.collection("userProfiles")
            .document(documentId)
            .set(userProfile)
            .addOnSuccessListener {
                scope.launch {

                insertOrUpdateUserProfileLocally(userProfile)
                }
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    private fun updateProfilePictureUrlInFirestore(userId: String, profilePictureUrl: String) {
        val userCollection = db.collection("userProfiles")
        userCollection.document(userId)
            .update("profilePictureUrl", profilePictureUrl)
            .addOnSuccessListener {
                // Profile picture URL updated successfully
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    private fun getStorageReference(uri: Uri): StorageReference {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        return storageReference.child("profile_pictures/${System.currentTimeMillis()}_${uri.lastPathSegment}")
    }

    fun uploadProfilePicture(userId: String, imageUri: Uri) {
        val storageReference = getStorageReference(imageUri)
        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Update the user's profile with the new profile picture URL in Firestore
                    updateProfilePictureUrlInFirestore(userId, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("ProfileRepository", "Failed to upload profile picture: ${e.message}")
            }
    }

    private fun UserProfile.toUserProfile(): UserProfile {
        // Convert the stored entity to the domain model
        return this
    }


    // Inside ProfileRepository
    suspend fun getSelectedTags(userId: String): List<UserTags> {
        return try {
            val snapshot = db.collection("selectedTags")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val selectedTags = mutableListOf<UserTags>()
            for (document in snapshot.documents) {
                val tag = document.toObject(UserTags::class.java)
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

    fun updateProfilePictureUrl(userId: String, toString: String) {
        val userCollection = db.collection("userProfiles")
        userCollection.document(userId)
            .update("profilePictureUrl", toString)
            .addOnSuccessListener {
                // Profile picture URL updated successfully
            }
            .addOnFailureListener {
                // Handle failure
            }

    }

    fun createUserProfile(
        userProfile: UserProfile,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        db.collection("userProfiles")
            .document(userProfile.userId)
            .set(userProfile)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


}
