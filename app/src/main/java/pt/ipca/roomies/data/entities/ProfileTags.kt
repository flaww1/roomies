package pt.ipca.roomies.data.entities

data class ProfileTags(
    val tagId: String,
    val tagType: TagType,
    val tagName: String,

)



data class UserTags(
    val userTagId: String?,
    val userId: String, // Foreign key referencing User table
    val tagId: String, // Foreign key referencing ProfileTags table
    val tagType: TagType, // Store the type of tag
    var isSelected: Boolean
)
enum class TagType {
    INTEREST,
    LIFESTYLE,
    LANGUAGE,
    // Add other types as needed
}

data class SelectedTag(
    val tagId: String,
    val tagName: String,
    val tagType: TagType
)