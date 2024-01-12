package pt.ipca.roomies.ui.authentication.registration


import pt.ipca.roomies.data.entities.UserProfile
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.User
import pt.ipca.roomies.data.repositories.ProfileRepository
import pt.ipca.roomies.data.repositories.RegistrationRepository

class RegistrationViewModel(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {



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

    private val _registrationState = MutableLiveData<RegistrationState>()

    val registrationState: LiveData<RegistrationState> get() = _registrationState

    // Function to update the registration state
    fun updateRegistrationState(newState: RegistrationState) {
        _registrationState.value = newState
    }

    // Enum class to represent different states of the registration process
    enum class RegistrationState {
        INITIAL,          // Initial state
        ROLE_SELECTION,   // Role selection
        PROFILE_INFO,     // Additional profile information
        USER_INTERESTS,   // User interests
        COMPLETED         // Registration completed
    }

    fun updateSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }


    fun updateUserProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
    }

    fun updateProfileTags(tags: List<ProfileTags>) {
        _profileTags.value = tags
    }

    fun updateUserId(userId: String) {
        _userId.value = userId
        Log.d("RegistrationViewModel", "Updated userId: $_userId")
    }



    fun register(firstName: String, lastName: String, email: String, password: String, userRole: String) {
        // Set up initial user data
        val user = User(
            userId = "",
            firstName = firstName,
            lastName = lastName,
            email = email,
            userRole = userRole,  // Pass the user role directly
            password = password,
            registrationDate = 0,
            userRating = 0
        )

        // Update LiveData in the ViewModel
        setUser(user)
    }


    private fun setUser(user: User) {
        _user.value = user
    }



    fun handleRegistrationError(message: String?) {
        _errorMessage.value = message
        // Clear the error message after handling it
        _errorMessage.value = null
    }
}
