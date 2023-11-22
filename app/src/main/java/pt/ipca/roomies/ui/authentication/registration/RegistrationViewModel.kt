package pt.ipca.roomies.ui.authentication.registration

import UserProfile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.ProfileTags
import User


// RegistrationViewModel.kt
class RegistrationViewModel : ViewModel() {
    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _profileTags = MutableLiveData<List<ProfileTags>>()
    val profileTags: LiveData<List<ProfileTags>> get() = _profileTags

    // Additional methods for validation, saving data, etc.

    fun updateUser(user: User) {
        _user.value = user
    }

    fun updateUserProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
    }

    fun updateProfileTags(tags: List<ProfileTags>) {
        _profileTags.value = tags
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        // Perform registration logic here
    }

    fun updateUserRole(role: String) {

    }
}
