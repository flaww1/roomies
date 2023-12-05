// LoginViewModel.kt
package pt.ipca.roomies.ui.authentication.login

import android.util.Log
import pt.ipca.roomies.data.repositories.LoginRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult
    private val loginRepository = LoginRepository()

    sealed class LoginResult {
        data class Success(val user: FirebaseUser) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Call the repository method for login
                val result = loginRepository.signIn(email, password)
                result?.let {
                    // Handle successful login
                    _loginResult.value = LoginResult.Success(it)
                    Log.d("LoginViewModel", "Login successful")
                } ?: run {
                    _loginResult.value = LoginResult.Error("Login failed. Check your credentials.")
                    Log.d("LoginViewModel", "Login failed")
                }
            } catch (e: Exception) {
                // Handle other exceptions (network issues, etc.)
                _loginResult.value = LoginResult.Error("Login failed. ${e.message}")
                Log.d("LoginViewModel", "Login failed with exception: ${e.message}")
            }
        }
    }

}
