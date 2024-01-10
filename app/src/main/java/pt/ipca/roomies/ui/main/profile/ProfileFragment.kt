package pt.ipca.roomies.ui.main.profile

import pt.ipca.roomies.data.entities.UserProfile
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserProfileDao
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.ProfileRepository
import pt.ipca.roomies.data.repositories.ProfileViewModelFactory
import pt.ipca.roomies.data.repositories.RoomRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ProfileFragment : Fragment() {

    private lateinit var userProfileDao: UserProfileDao
    private lateinit var roomDao: RoomDao
    private lateinit var profileRepository: ProfileRepository
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: ProfileViewModel

    private lateinit var combinedProfileLayout: View
    private lateinit var createProfileLayout: View
    private lateinit var displayProfileLayout: View
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var textViewNameAge: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewBio: TextView
    private lateinit var linearLayoutTags: LinearLayout

    private lateinit var textViewMessage: TextView
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var userProfile: UserProfile? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        profileRepository = ProfileRepository(userProfileDao)
        roomRepository = RoomRepository(userProfileDao, roomDao)

        viewModel = ViewModelProvider(this, ProfileViewModelFactory(roomRepository, profileRepository))[ProfileViewModel::class.java]

        // Inflate the combined profile layout using data binding
        combinedProfileLayout = inflater.inflate(R.layout.profile_combined_layout, container, false)

        // Find references to the individual profile creation and display layouts
        createProfileLayout = combinedProfileLayout.findViewById(R.id.layoutProfileCreation)
        displayProfileLayout = combinedProfileLayout.findViewById(R.id.layoutProfileDisplay)

        // Initialize references to profile display views
        imageViewProfilePicture = combinedProfileLayout.findViewById(R.id.imageViewProfilePicture)
        textViewNameAge = combinedProfileLayout.findViewById(R.id.textViewNameAge)
        textViewLocation = combinedProfileLayout.findViewById(R.id.textViewLocation)
        textViewBio = combinedProfileLayout.findViewById(R.id.textViewBio)
        linearLayoutTags = combinedProfileLayout.findViewById(R.id.linearLayoutTags)

        textViewMessage = createProfileLayout.findViewById<TextView>(R.id.textViewMessage)


        // Return the combined layout with initial visibility settings
        return combinedProfileLayout
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                showProfileDisplayLayout(userProfile)
                Log.d("ProfileFragment", "User Profile: $userProfile")
            } else {
                showProfileCreationLayout()
                Log.d("ProfileFragment", "User does not have a profile")
            }
        }



        viewModel.getUserProfile()
    }





    private fun showProfileCreationLayout() {
        createProfileLayout.visibility = View.VISIBLE
        displayProfileLayout.visibility = View.GONE
        textViewMessage.text = "You don't have a profile yet. Create one now!"
    }

    private fun showProfileDisplayLayout(userProfile: UserProfile) {

        Log.d("ProfileFragment", "User Profile Display: $userProfile")

        // Set visibility for profile creation layout
        createProfileLayout.visibility = View.GONE

        // Set visibility for profile display layout
        displayProfileLayout.visibility = View.VISIBLE

        // Set profile picture (replace "placeholder_url" with the actual URL from userProfile)
        userProfile.profilePictureUrl.let {
            Glide.with(this).load(it).into(imageViewProfilePicture)
        }


        val nameText = currentUser?.displayName ?: "Unknown User"
        textViewNameAge.text = nameText

        // Calculate and set user age from birth date
        val age = calculateAge(userProfile.birthDate)
        val ageText = "$age years old"

        // Combine name and age text
        val nameAgeText = "$nameText, $ageText"
        textViewNameAge.text = nameAgeText

        // Set location
        textViewLocation.text = userProfile.location

        // Set bio
        textViewBio.text = userProfile.bio

        // Clear existing tags
        linearLayoutTags.removeAllViews()

        // Populate tags (replace "userProfile.tags" with the actual list of tags)
        /*
        for (tag in userProfile.tags) {
            val tagTextView = TextView(requireContext())
            tagTextView.text = tag
            // Customize the appearance of the tagTextView if needed
            linearLayoutTags.addView(tagTextView)
        }*/
    }


    private fun calculateAge(birthDate: String?): Int {
        birthDate?.let {
            // Convert birth date string to Date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate: Date? = try {
                dateFormat.parse(birthDate)
            } catch (e: ParseException) {
                null
            }

            parsedDate?.let {
                // Calculate age using Calendar
                val birthCalendar = Calendar.getInstance()
                birthCalendar.time = it

                val currentCalendar = Calendar.getInstance()

                var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }

                return age
            }
        }
        return 0
    }



}
