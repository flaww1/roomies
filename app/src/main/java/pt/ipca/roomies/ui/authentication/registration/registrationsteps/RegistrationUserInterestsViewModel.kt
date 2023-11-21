package pt.ipca.roomies.ui.authentication.registration.registrationsteps

// Inside RegistrationUserInterestsViewModel.kt

import androidx.lifecycle.ViewModel

class RegistrationUserInterestsViewModel : ViewModel() {

    // List of available interest tags
    private val availableInterestTags = listOf(
        "Sports",
        "Technology",
        "Music",
        // Add more interests as needed
    )

    // List to store user-selected interest tags
    private val selectedInterestTags = mutableListOf<String>()

    // Function to get the list of available interest tags
    fun getAvailableInterestTags(): List<String> {
        return availableInterestTags
    }

    // Function to get the list of user-selected interest tags
    fun getSelectedInterestTags(): List<String> {
        return selectedInterestTags
    }

    // Function to update the user-selected interest tags
    fun updateSelectedInterestTags(tag: String, isSelected: Boolean) {
        if (isSelected) {
            // Add the tag to the selected list if it's selected
            if (!selectedInterestTags.contains(tag)) {
                selectedInterestTags.add(tag)
            }
        } else {
            // Remove the tag from the selected list if it's deselected
            selectedInterestTags.remove(tag)
        }
    }
}
