package pt.ipca.roomies.data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.entities.User
import pt.ipca.roomies.data.local.AppDatabase
import java.util.Calendar

class RegistrationRepository(private val applicationContext: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val database: AppDatabase = AppDatabase.getDatabase(applicationContext)
    // Room-related functions

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            database.userDao().insertUser(user)
        }
    }

    suspend fun getAllUsers(): List<User> {
        return database.userDao().getAllUsers()
    }

    suspend fun getUserById(userId: String): User? {
        return database.userDao().getUserById(userId)
    }

    suspend fun insertOrUpdateUserLocally(user: User) {
        withContext(Dispatchers.IO) {
            database.userDao().insertUser(user)
        }
    }

    fun updateUserInFirestore(updatedUser: User) {
        Log.d("RegistrationRepository", "Updating user in Firestore: $updatedUser")

        val userId = updatedUser.userId
        if (userId.isNotEmpty()) {
            val userDocument = firestore.collection("users").document(userId)

            userDocument.set(updatedUser)
                .addOnSuccessListener {
                    // Update successful
                    Log.d("RegistrationRepository", "User role updated successfully: $updatedUser")
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    Log.e("RegistrationRepository", "Error updating user role", e)
                }
        } else {
            Log.e("RegistrationRepository", "User ID is empty, cannot update user role")
        }
    }

    suspend fun storeUserInFirestore(
        userId: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        userRole : String
    ) {
        // Create a user object with basic information
        val user = User(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            userRole = userRole,
            password = password,
            registrationDate = Calendar.getInstance().timeInMillis,
            userRating = 0
            // Add other fields as needed
        )

        // Store user information in Room database
        insertOrUpdateUserLocally(user)

        // Store user information in Firestore asynchronously
        withContext(Dispatchers.IO) {
            firestore.collection("users").document(userId).set(user)
                .addOnCompleteListener { userTask ->
                    if (!userTask.isSuccessful) {
                        // Handle the failure
                        Log.e("RegistrationRepository", "Failed to store user in Firestore")
                    } else {
                        // Handle success if needed
                        Log.d("RegistrationRepository", "User stored successfully in Firestore")
                    }
                }
        }
    }

}
