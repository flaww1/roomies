package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.ProfileTags

class RegistrationUserInterestsViewModel : ViewModel() {
    private val _selectedInterestTags: MutableLiveData<List<ProfileTags>> = MutableLiveData()
    val selectedInterestTags: LiveData<List<ProfileTags>> get() = _selectedInterestTags

    // List of available interest tags
    private val availableInterestTags = listOf(
        ProfileTags("Sports"),
        ProfileTags("Technology"),
        ProfileTags("Music"),
        // Add more interests as needed
    )

    // Function to get the list of available interest tags
    fun getAvailableInterestTags(): List<ProfileTags> {
        return availableInterestTags
    }

    // Function to get the list of user-selected interest tags
    fun getSelectedInterestTags(): LiveData<List<ProfileTags>> {
        return selectedInterestTags
    }

    // Function to update the user-selected interest tags
    fun updateSelectedInterestTags(tag: ProfileTags, isSelected: Boolean) {
        // Get the current list of selected interest tags
        val currentTags = _selectedInterestTags.value ?: emptyList()

        // Create a new list for the updated selected interest tags
        val updatedTags = if (isSelected) {
            // Add the tag to the list if it's selected and it's not already in the list
            if (tag !in currentTags) {
                currentTags + tag
            } else {
                currentTags
            }
        } else {
            // Remove the tag from the list if it's deselected
            currentTags - tag
        }

        // Set the value of the selectedInterestTags LiveData to the updated list
        _selectedInterestTags.value = updatedTags
    }

    fun updateProfileTags(profileTags: List<ProfileTags>) {
        _selectedInterestTags.value = profileTags
    }
}

