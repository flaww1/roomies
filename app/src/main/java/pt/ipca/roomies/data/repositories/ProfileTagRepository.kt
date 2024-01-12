package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.dao.ProfileTagsDao
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

class ProfileTagRepository(private val profileTagsDao: ProfileTagsDao) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun createTag(tag: ProfileTags) {
        // Add tag to local Room database
        profileTagsDao.insertProfileTags(listOf(tag))

        // Add tag to Firestore
        db.collection("profileTags")
            .add(tag)
            .addOnSuccessListener { documentReference ->
                val tagId =
                documentReference.id
                // Notify success if needed
            }
            .addOnFailureListener {
                // Notify failure if needed
            }
    }

    suspend fun associateTagWithUser(userId: String, tagId: String, tagType: TagType, isSelected: Boolean) {
        val userTag = UserTags(userId, tagId, tagType, isSelected)

        // Query the UserTags table to find an existing entry for this user and tag
        db.collection("userTags")
            .whereEqualTo("userId", userId)
            .whereEqualTo("tagId", tagId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No existing entry found, so create a new one
                    db.collection("userTags").add(userTag)
                } else {
                    // Existing entry found, so update it
                    for (document in documents) {
                        db.collection("userTags").document(document.id).set(userTag)
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error associating tag with user: $e")
            }
    }

    suspend fun getTagsByType(tagType: TagType): List<ProfileTags> {
        return try {
            db.collection("profileTags")
                .whereEqualTo("tagType", tagType.name)
                .get()
                .await()
                .toObjects(ProfileTags::class.java)
        } catch (e: Exception) {
            // Handle any exceptions (e.g., network issues)
            emptyList()
        }
    }

    fun getAllTagTypes(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        // Use Firestore or any other data source to fetch all unique tag types from "profileTags" collection
        val profileTagsCollection = FirebaseFirestore.getInstance().collection("profileTags")

        profileTagsCollection.get()
            .addOnSuccessListener { documents ->
                val uniqueTagTypes = documents
                    .mapNotNull { document ->
                        document.getString("tagType")
                    }
                    .distinct()

                onSuccess(uniqueTagTypes)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    suspend fun getSelectedTags(userId: String): List<UserTags>? {
        return try {
            db.collection("userTags")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isSelected", true)
                .get()
                .await()
                .toObjects(UserTags::class.java)
        } catch (e: Exception) {
            // Handle any exceptions (e.g., network issues)
            null
        }
    }
}
