package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationUserInterestsViewModel : ViewModel() {
    private val selectedInterestTags: MutableLiveData<List<String>> = MutableLiveData()

    // List of available interest tags
    private val availableInterestTags = listOf(
        "Sports",
        "Technology",
        "Music",
        // Add more interests as needed
    )

    // Function to get the list of available interest tags
    fun getAvailableInterestTags(): List<String> {
        return availableInterestTags
    }

    // Function to get the list of user-selected interest tags
    fun getSelectedInterestTags(): MutableLiveData<List<String>> {
        return selectedInterestTags
    }

    // Function to update the user-selected interest tags
    fun updateSelectedInterestTags(tag: String, isSelected: Boolean) {
        // Get the current list of selected interest tags
        val currentTags = selectedInterestTags.value ?: emptyList()

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
        selectedInterestTags.value = updatedTags
    }
}
