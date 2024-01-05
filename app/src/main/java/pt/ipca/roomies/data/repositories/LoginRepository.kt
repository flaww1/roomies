package pt.ipca.roomies.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.dao.UserDao
import pt.ipca.roomies.data.entities.User

class LoginRepository(private val userDao: UserDao, private val auth: FirebaseAuth) {

    suspend fun signIn(email: String, password: String): User? {
        try {
            // Sign in with Firebase
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d("LoginRepository", "Firebase Sign-In Result: $result")
            val firebaseUser = result.user

            // Retrieve user from Room or create a new one if not exists
            return firebaseUser?.uid?.let {
                userDao.getUserById(it) ?: User(userId = it, email = email)
            }
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("LoginRepository", "Error signing in: ${e.message}")
            throw e
        }
    }


}
