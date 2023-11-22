import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.databinding.FragmentRegistrationUserInterestsBinding
import pt.ipca.roomies.ui.authentication.registration.registrationsteps.RegistrationUserInterestsViewModel
import android.os.Bundle
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel


class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var viewModelRegistration: RegistrationViewModel
    private lateinit var adapter: InterestTagsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationUserInterestsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(RegistrationUserInterestsViewModel::class.java)
        viewModelRegistration = ViewModelProvider(requireActivity()).get(RegistrationViewModel::class.java)

        // Initialize Adapter here
        adapter = InterestTagsAdapter(listOf()) { tag, isSelected ->
            viewModel.updateSelectedInterestTags(tag, isSelected)
        }
        binding.recyclerViewInterests.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())
        // Implement UI interactions
        binding.nextButton.setOnClickListener {
            // Navigate to the UserProfileInfoFragment
            val selectedInterests = viewModel.getSelectedInterestTags().value
            val profileTags = selectedInterests.map { ProfileTags(it) }
            viewModel.updateProfileTags(profileTags)
            navigateToHomePage()
            onFinalStepCompleted( )


        }

        // Implement back button functionality
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }
        // Observe changes in the selected interest tags
        viewModel.getSelectedInterestTags().observe(viewLifecycleOwner) { selectedTags ->
            // Handle changes, if needed

            // For example, update UI to reflect selected tags
        }

        // Populate the adapter with data
        adapter.updateData(viewModel.getAvailableInterestTags())
    }
    private fun navigateToHomePage() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }

    fun onFinalStepCompleted(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String,
        bio: String,
        location: String,
        pronouns: String,
        gender: String,
        occupation: String,
        profilePictureUrl: String
    ) {
        val viewModel = ViewModelProvider(requireActivity()).get(RegistrationViewModel::class.java)

        // Observe user profile data
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                // Assuming there's a method to convert UserProfile to a map
                val userProfileMap = userProfile.toMap()

                // Additional user data
                val user = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                    "password" to password,
                    "role" to role,
                    "registrationDate" to FieldValue.serverTimestamp()
                    // Add other user data here
                )

                // Combine user and user profile data
                val userData = user + userProfileMap

                // Save the combined data to Firestore or perform any other necessary actions
                saveUserDataToFirestore(userData)
            }
        }
    }

    private fun saveUserDataToFirestore(userData: Map<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        Firebase.auth.currentUser?.uid?.let { userId ->
            // Add the user document to the "users" collection
            usersCollection.document(userId).set(userData)
        }

        // You can also add additional logic here if needed
    }

    // This extension function is used to convert UserProfile to a map
    private fun UserProfile.toMap(): Map<String, Any> {
        return mapOf(
            "profilePictureUrl" to profilePictureUrl,
            "location" to location,
            "bio" to bio,
            "pronouns" to pronouns,
            "gender" to gender,
            "occupation" to occupation
            // Add other profile fields as needed
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
