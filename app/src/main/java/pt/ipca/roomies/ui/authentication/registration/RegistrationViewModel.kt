import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.roomies.data.entities.ProfileTags

// RegistrationViewModel.kt
class RegistrationViewModel : ViewModel() {
    // get current user

    val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _profileTags = MutableLiveData<List<ProfileTags>>()
    val profileTags: LiveData<List<ProfileTags>> get() = _profileTags

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
        // Implement update user role logic
    }

    fun saveUserDataToFirestore(user: User, userProfile: UserProfile) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { registrationTask ->
                if (registrationTask.isSuccessful) {
                    val userId = registrationTask.result?.user?.uid ?: return@addOnCompleteListener

                    user.userId = userId
                    saveUserToFirestore(user, userId)
                    saveUserProfileToFirestore(userProfile, userId)
                } else {
                    _errorMessage.value = registrationTask.exception?.message
                }
            }
    }

    private fun saveUserToFirestore(user: User, userId: String) {
        firestore.collection("users").document(userId).set(user)
            .addOnCompleteListener { userTask ->
                if (!userTask.isSuccessful) {
                    _errorMessage.value = "Failed to save user information"
                }
            }
    }

    private fun saveUserProfileToFirestore(userProfile: UserProfile, userId: String) {
        firestore.collection("userProfiles").document(userId).set(userProfile)
            .addOnCompleteListener { profileTask ->
                if (!profileTask.isSuccessful) {
                    _errorMessage.value = "Failed to save user profile information"
                }
            }
    }


}
