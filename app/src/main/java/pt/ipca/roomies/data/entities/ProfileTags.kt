package pt.ipca.roomies.data.entities

data class ProfileTags(
    val tagId: String = "",
    val tagName: String = "",
    val tagType: TagType = TagType.Interest// Replace with an appropriate default value
) {
    // Default constructor with default values
    constructor() : this("", "", TagType.Interest)
}

data class UserTags(
    val userTagId: String?,
    val userId: String, // Foreign key referencing User table
    val tagId: String, // Foreign key referencing ProfileTags table
    val tagType: TagType, // Store the type of tag
    var isSelected: Boolean
)
enum class TagType {
    Language, Interest, Personality
}

data class SelectedTag(
    val tagId: String,
    val tagName: String,
    val tagType: TagType
)