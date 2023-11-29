import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.roomies.data.entities.ProfileTags

// RegistrationViewModel.kt
class RegistrationViewModel : ViewModel() {

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    fun setUser(user: User) {
        _user.value = user
    }

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _profileTags = MutableLiveData<List<ProfileTags>?>()
    val profileTags: LiveData<List<ProfileTags>?> get() = _profileTags

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

    fun register(firstName: String, lastName: String, email: String, password: String) {
        // Perform registration logic here
    }

    fun updateUserRole(role: String) {
        // Implement update user role logic

    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            // User is successfully registered, perform actions like navigating to the next screen
        } else {
            // Handle the case where currentUser is null (registration failed)
            _errorMessage.value = "User registration failed"
        }
    }


    fun reset() {
        _user.value = null
        _userProfile.value = null
        _profileTags.value = null
        _errorMessage.value = null
        _selectedImageUri.value = null
    }


}
