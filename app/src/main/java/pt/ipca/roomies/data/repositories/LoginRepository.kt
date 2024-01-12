package pt.ipca.roomies.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.dao.UserDao
import pt.ipca.roomies.data.entities.User

class LoginRepository(private val userDao: UserDao) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    suspend fun signIn(email: String, password: String): User? {
        try {
            // Sign in with Firebase
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d("LoginRepository", "Firebase Sign-In Result: $result")
            val firebaseUser = result.user

            // Retrieve user from Room or create a new one if not exists
            val user = firebaseUser?.uid?.let {
                userDao.getUserById(it) ?: User(userId = it, email = email)
            }

            // Fetch and set the user role
            user?.let {
                val userRole = fetchUserRole(firebaseUser)
                it.userRole = userRole
            }

            return user
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("LoginRepository", "Error signing in: ${e.message}")
            throw e
        }
    }

    suspend fun fetchUserRole(firebaseUser: FirebaseUser?): String {
        return firebaseUser?.uid?.let {
            val userDocument = auth.uid?.let { it1 -> userDao.getUserById(it1) }
            userDocument?.userRole ?: ""
        } ?: ""
    }

    suspend fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


}
