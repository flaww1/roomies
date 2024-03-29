package pt.ipca.roomies.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.User
import pt.ipca.roomies.data.repositories.LoginRepository

// pt.ipca.roomies.ui.authentication.login.LoginViewModel.kt
//LiveData para atualizacao da interface do utilizador, conforme o resultado do login
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() { //view model, para parametro do constr tem o loginRepository, intermedeia o repositorio e a interface

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    sealed class LoginResult { //restringe uso de herança, representa o resultado do login
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch { //operacoes assincronas aut
            try {
                val user = loginRepository.signIn(email, password)
                user?.let {
                    _loginResult.value = LoginResult.Success(user)
                } ?: run {
                    _loginResult.value = LoginResult.Error("Login failed. Check your credentials.")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Login failed. ${e.message}")
            }
        }
    }

}
