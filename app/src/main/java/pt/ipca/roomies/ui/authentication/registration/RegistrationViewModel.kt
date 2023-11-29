package pt.ipca.roomies.ui.authentication.registration

import User
import UserProfile
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.repositories.RegistrationRepository

class RegistrationViewModel : ViewModel() {

    private val registrationRepository = RegistrationRepository(this)
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId
    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _userProfile = MutableLiveData<UserProfile?>()

    private val _profileTags = MutableLiveData<List<ProfileTags>?>()

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri

    fun updateSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun updateUser(user: User) {
        _user.value = user
    }

    fun updateUserProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
    }

    fun updateProfileTags(tags: List<ProfileTags>) {
        _profileTags.value = tags
    }

    suspend fun registerUser(): Boolean {
        val user = _user.value
        val userProfile = _userProfile.value

        return if (user != null && userProfile != null) {
            registrationRepository.registerUser(user, userProfile)
        } else {
            // Handle the case when user or userProfile is null
            false
        }
    }
    fun updateUserId(userId: String) {
        _userId.value = userId
    }

    fun updateUserRole(role: String) {
        _user.value?.let { user ->
            val updatedUser = user.copy(userRole = role)
            updateUser(updatedUser)
        }
    }
    fun register(firstName: String, lastName: String, email: String, password: String) {
        // Set up initial user data
        val user = User(
            userId = "",
            firstName = firstName,
            lastName = lastName,
            email = email,
            userRole = "",
            password = password,
            registrationDate = 0,
            userRating = 0
        )
        val userProfile = UserProfile(
            userId = "",
            bio = "",
            location = "",
            gender = "",
            occupation = "",
            birthDate = "",  // Provide a default value
            profilePictureUrl = "",  // Provide a default value
            selectedTags = emptyList(),  // Provide a default value
            userProfileId = ""  // Provide a default value
        )

        // Update LiveData in the ViewModel
        setUser(user)
        updateUserProfile(userProfile)
        updateSelectedImageUri(null)
        updateProfileTags(emptyList())
    }

    private fun setUser(user: User) {
        _user.value = user
    }
    fun reset() {
        _user.value = null
        _userProfile.value = null
        _profileTags.value = null
        _errorMessage.value = null
        _selectedImageUri.value = null
    }



    fun handleRegistrationError(message: String?) {
        _errorMessage.value = message

    }
}
