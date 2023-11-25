package pt.ipca.roomies.ui.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    val isLoggedIn = MutableLiveData<Boolean>()
}
