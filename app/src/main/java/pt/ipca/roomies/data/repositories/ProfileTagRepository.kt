import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

class ProfileTagsRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createTag(tag: ProfileTags, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("profileTags")
            .add(tag)
            .addOnSuccessListener { documentReference ->
                val tagId = documentReference.id
                onSuccess.invoke(tagId)
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun associateTagWithUser(userId: String, tagId: String, tagType: TagType, isSelected: Boolean) {

        val userTag = UserTags(userTagId = null ,userId, tagId, tagType, isSelected)

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

    fun getTagsByType(tagType: TagType, onSuccess: (List<ProfileTags>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("profileTags")
            .whereEqualTo("tagType", tagType)
            .get()
            .addOnSuccessListener { documents ->
                val tags = mutableListOf<ProfileTags>()
                for (document in documents) {
                    val tag = document.toObject(ProfileTags::class.java)
                    tags.add(tag)
                }
                onSuccess.invoke(tags)
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

}

