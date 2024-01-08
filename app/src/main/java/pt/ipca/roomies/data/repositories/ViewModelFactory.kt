package pt.ipca.roomies.data.repositories

import pt.ipca.roomies.ui.authentication.login.LoginViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
import pt.ipca.roomies.ui.main.HomeViewModel
import pt.ipca.roomies.ui.main.users.habitation.HabitationViewModel
import pt.ipca.roomies.ui.main.users.room.RoomViewModel
import pt.ipca.roomies.ui.main.profile.ProfileViewModel

class LoginViewModelFactory(private val loginRepository: LoginRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

class ProfileViewModelFactory(
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(roomRepository, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RegistrationViewModelFactory(
    private val registrationRepository: RegistrationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(registrationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class RoomViewModelFactory(private val roomRepository: RoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomViewModel(roomRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class HabitationViewModelFactory(private val habitationDao: HabitationDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitationViewModel(HabitationRepository(habitationDao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HomeViewModelFactory(private val cardRepository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(cardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
