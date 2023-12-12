package pt.ipca.roomies.ui.main.profile

import UserProfile
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentProfileCreateBinding
import pt.ipca.roomies.databinding.FragmentProfileDisplayBinding

class ProfileFragment : Fragment() {

    private lateinit var createBinding: FragmentProfileCreateBinding
    private lateinit var displayBinding: FragmentProfileDisplayBinding
    private val viewModel by lazy { ProfileViewModel() }

    // Example user profile, replace this with the actual user profile data
    private var userProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate both layouts using data binding
        createBinding = FragmentProfileCreateBinding.inflate(inflater, container, false)
        displayBinding = FragmentProfileDisplayBinding.inflate(inflater, container, false)

        // Return the root view based on the user profile state
        return if (userProfile == null) createBinding.root else displayBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if the user has a profile
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                // Display the profile display layout
                showProfileDisplayLayout(userProfile)
            } else {
                // Display the profile creation layout
                showProfileCreationLayout()
            }
        }

        // Fetch the user profile
        viewModel.getUserProfile()
    }


    private fun showProfileCreationLayout() {
        // Customize the profile creation layout as needed
        createBinding.textViewMessage.text = getString(R.string.create_profile_message)
        createBinding.buttonCreateProfile.visibility = View.VISIBLE

        // Set click listener for the "Create Profile" button
        createBinding.buttonCreateProfile.setOnClickListener {
            // Navigate to the profile creation screen or perform the necessary actions
            findNavController().navigate(R.id.action_profileFragment_to_profileUserInfoFragment)

        }
    }

    private fun showProfileDisplayLayout(userProfile: UserProfile) {
        // Customize the profile display layout as needed
        // Display user profile information in the layout
        /*displayBinding.imageViewProfilePicture.setImageResource(userProfile.profilePictureUrl)
        displayBinding.textViewNameAge.text = getString(
            R.string.profile_name_age,
            userProfile.name,
            userProfile.age.toString()
        )

         */
        displayBinding.textViewLocation.text = userProfile.location
        displayBinding.textViewBio.text = userProfile.bio

        // Set click listener for the "Edit Profile" button
        displayBinding.buttonEditProfile.setOnClickListener {

        }
    }
}
