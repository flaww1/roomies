package pt.ipca.roomies.data.entities

data class User(
    var userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    var registrationDate: Long,
    val userRating: Int,
    val userRole: String,
)

data class UserPreferences(
    val minAge: Int = 0,
    val maxAge: Int = 0,
    val preferredGender: String = "",
    val preferredLocation: String = ""
)



