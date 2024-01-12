package pt.ipca.roomies.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey
    var userId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    @get:Exclude
    var password: String? = null,
    var registrationDate: Long? = null,
    val userRating: Int? = null,
    var userRole: String? = null
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, 0, "")

    val fullName: String
        get() = "$firstName $lastName"


}



data class UserPreferences(
    val minAge: Int = 0,
    val maxAge: Int = 0,
    val preferredGender: String = "",
    val preferredLocation: String = ""
)



