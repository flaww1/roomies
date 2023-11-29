data class User(
    var userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    var registrationDate: String,
    val userRating: Int,
    val userRole: Roles,
)

data class UserPreferences(
    val minAge: Int = 0,
    val maxAge: Int = 0,
    val preferredGender: String = "",
    val preferredLocation: String = ""
)

enum class Roles
{
    ADMIN,
    USER,
    TENANT,
    LANDLORD,
    LEADER;
}

