package pt.ipca.roomies.data.repositories

import UserProfile
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createUserProfile(userProfile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Add your logic to store the user profile data in Firestore
        db.collection("userProfiles")
            .add(userProfile)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun updateUserProfile(userProfile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Add your logic to update the user profile data in Firestore
        db.collection("userProfiles")
            .document(userProfile.userProfileId)
            .set(userProfile)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    // Add other methods as needed for profile-related operations

    // Example method to get a user profile by user ID
    fun getUserProfileByUserId(userId: String, onSuccess: (UserProfile?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("userProfiles")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onSuccess.invoke(null)
                } else {
                    val userProfile = documents.documents[0].toObject(UserProfile::class.java)
                    onSuccess.invoke(userProfile)
                }
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    private fun updateProfilePictureUrl(userId: String, profilePictureUrl: String) {
        // Update the user's profile with the new profile picture URL in Firestore
        val userCollection = firestore.collection("users")
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

    public fun uploadProfilePicture(userId: String, imageUri: Uri) {
        // Your image upload logic here
        // Use Firebase Storage or any other method to upload the image

        // Example using Firebase Storage
        val storageReference = getStorageReference(imageUri)
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // If the upload is successful, get the download URL
                storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Update the user's profile with the new profile picture URL in Firestore
                    updateProfilePictureUrl(userId, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("ProfileUserInterestsFragment", "Failed to upload profile picture: ${e.message}")
            }
    }
}
