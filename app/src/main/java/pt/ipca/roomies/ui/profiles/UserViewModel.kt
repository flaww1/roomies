package pt.ipca.roomies.ui.profiles

import User
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.ui.main.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun updateUserProfile(user: User) {
        userRepository.updateUserProfile(user)
    }

    // Implement other ViewModel logic for user-related operations
}