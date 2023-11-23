data class User(
    var userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val registrationDate: String,
    val userRating: Int,
    val userRole: Roles,
)

enum class Roles
{
    ADMIN,
    USER,
    TENANT,
    LANDLORD,
    LEADER;
}