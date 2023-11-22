// LoginViewModel.kt
package pt.ipca.roomies.ui.authentication.login

import LoginRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            // Call the repository method for login
            val result = loginRepository.signIn(email, password)

            // Handle the result accordingly (update UI, show error, etc.)
        }
    }
}
