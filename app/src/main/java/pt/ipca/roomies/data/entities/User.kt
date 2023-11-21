package pt.ipca.roomies.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val registrationDate: Long,
    val userRating: Int,
    val userRole: String,
    val lastLogin: Long
)