import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean>
        get() = _loginResult

    init {
        // Initialize any other necessary logic here
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun performLogin() {
        // Perform login logic here
        // For simplicity, just check if username and password are not empty
        val isLoginSuccessful = !username.value.isNullOrBlank() && !password.value.isNullOrBlank()
        _loginResult.value = isLoginSuccessful
    }
}
