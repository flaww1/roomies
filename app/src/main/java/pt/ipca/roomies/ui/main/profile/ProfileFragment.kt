package pt.ipca.roomies.ui.main.profile

import UserProfile
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentProfileCreateBinding
import pt.ipca.roomies.databinding.FragmentProfileDisplayBinding

class ProfileFragment : Fragment() {

    private lateinit var createBinding: FragmentProfileCreateBinding
    private lateinit var displayBinding: FragmentProfileDisplayBinding

    // Example user profile, replace this with the actual user profile data
    private var userProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate both layouts using data binding
        createBinding = FragmentProfileCreateBinding.inflate(inflater, container, false)
        displayBinding = FragmentProfileDisplayBinding.inflate(inflater, container, false)

        // Return one of the root views based on the user profile state
        return if (userProfile == null) createBinding.root else displayBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if the user has a profile
        if (userProfile == null) {
            // Display the profile creation layout
            showProfileCreationLayout()
        } else {
            // Display the profile display layout
            showProfileDisplayLayout(userProfile!!)
        }
    }

    private fun showProfileCreationLayout() {
        // Customize the profile creation layout as needed
        createBinding.textViewMessage.text = getString(R.string.create_profile_message)
        createBinding.buttonCreateProfile.visibility = View.VISIBLE

        // Set click listener for the "Create Profile" button
        createBinding.buttonCreateProfile.setOnClickListener {
            // Navigate to the profile creation screen or perform the necessary actions
            // (e.g., start a new activity or replace the fragment)
        }
    }

    private fun showProfileDisplayLayout(userProfile: UserProfile) {
        // Customize the profile display layout as needed
        //displayBinding.textViewNameAge.text = getString(R.string.profile_name_age, userProfile.name, userProfile.age)
        // Populate other views based on the user's profile data

        // Set click listener for the "Edit Profile" button
        displayBinding.buttonEditProfile.setOnClickListener {
            // Navigate to the profile editing screen or perform the necessary actions
            // (e.g., start a new activity or replace the fragment)
        }
    }

}
