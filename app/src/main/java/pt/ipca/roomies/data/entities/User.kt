package pt.ipca.roomies.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var userId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    @get:Exclude
    val password: String? = null,
    var registrationDate: Long? = null,
    val userRating: Int? = null,
    val userRole: String? = null
)



data class UserPreferences(
    val minAge: Int = 0,
    val maxAge: Int = 0,
    val preferredGender: String = "",
    val preferredLocation: String = ""
)



