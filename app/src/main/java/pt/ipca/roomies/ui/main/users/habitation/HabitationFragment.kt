package pt.ipca.roomies.ui.main.users.habitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.entities.HabitationAmenities
import pt.ipca.roomies.data.entities.SecurityMeasures
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory


class HabitationFragment : Fragment() {

    private lateinit var habitationRecyclerView: RecyclerView
    private lateinit var habitationsAdapter: HabitationAdapter
    private lateinit var habitationDao: HabitationDao
    private val viewModel: HabitationViewModel by viewModels {
        HabitationViewModelFactory(habitationDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and its Adapter
        habitationRecyclerView = view.findViewById(R.id.habitationRecyclerView)
        habitationRecyclerView.layoutManager = LinearLayoutManager(context)
        habitationsAdapter = HabitationAdapter(mutableListOf(), createHabitationClickListener())
        habitationRecyclerView.adapter = habitationsAdapter

        habitationDao = AppDatabase.getDatabase(requireContext()).habitationDao()

        // Fetch habitations by landlord ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getHabitationsByLandlordId(currentUser.uid)
        }


        // Observe the habitations LiveData
        viewModel.habitations.observe(viewLifecycleOwner, Observer { habitations ->
            Log.d("HabitationFragment", "Loaded Habitations: $habitations")

            val updatedHabitations = mutableListOf<Habitation>()
            // Filter habitations that belong to the current user
            habitations.forEach { habitation ->
                if (habitation.landlordId == currentUser?.uid) {
                    updatedHabitations.add(habitation)
                }
            }

            // Update RecyclerView adapter with the modified habitations list
            habitationsAdapter.updateData(updatedHabitations)
        })

        // Observe the creation success LiveData
        viewModel.habitationCreationSuccess.observe(viewLifecycleOwner) { documentId ->
            documentId?.let {
                // The habitation has been created successfully, use the documentId if needed
                viewModel.refreshHabitations()
            }
        }

        // Observe the deletion success LiveData
        viewModel.habitationDeletionSuccess.observe(viewLifecycleOwner) { deletionSuccess ->
            if (deletionSuccess == true) {
                // The habitation has been deleted successfully, use this information as needed
                viewModel.refreshHabitations()
            }
        }

        // Set up FloatingActionButton click listener
        val fabCreateHabitation: FloatingActionButton = view.findViewById(R.id.fabCreateHabitation)
        fabCreateHabitation.setOnClickListener {
            // Navigate to the habitation creation screen
            findNavController().navigate(R.id.action_habitationFragment_to_createHabitationFragment)
        }

    }

    // Function to create the HabitationClickListener
    private fun createHabitationClickListener(): HabitationAdapter.OnHabitationClickListener {
        return object : HabitationAdapter.OnHabitationClickListener {
            override fun onHabitationClick(habitation: Habitation) {
                Log.d("HabitationFragment", "Clicked on Habitation: $habitation")
                // Set the selected habitation in the ViewModel
                viewModel.selectHabitation(habitation)
                // Pass the habitationId as a bundle argument to the RoomFragment
                val bundle = Bundle().apply {
                    putString("habitationId", habitation.habitationId)
                }
                findNavController().navigate(R.id.action_habitationFragment_to_roomFragment, bundle)
            }

            override fun onEditHabitationClick(habitation: Habitation) {
                // Navigate to the habitation editing screen
                viewModel.selectHabitation(habitation)
                val bundle = Bundle().apply {
                    // Pass the existing fields as arguments
                    putString("address", habitation.address)
                    putString("city", habitation.city.name) // Assuming city is an enum, use its name
                    putString("numberOfRooms", habitation.numberOfRooms.toString())
                    putString("numberOfBathrooms", habitation.numberOfBathrooms.toString())
                    putString("habitationType", habitation.habitationType.type)
                    putString("description", habitation.description)
                    putBoolean("internet", HabitationAmenities.INTERNET in habitation.habitationAmenities)
                    putBoolean("parking", HabitationAmenities.PARKING in habitation.habitationAmenities)
                    putBoolean("kitchen", HabitationAmenities.KITCHEN in habitation.habitationAmenities)
                    putBoolean("laundry", HabitationAmenities.LAUNDRY in habitation.habitationAmenities)
                    putBoolean("petsAllowed", habitation.petsAllowed)
                    putString("smokingPolicy", habitation.smokingPolicy.name) // Assuming smokingPolicy is an enum, use its name
                    putString("noiseLevel", habitation.noiseLevel.name) // Assuming noiseLevel is an enum, use its name
                    putString("guestPolicy", habitation.guestPolicy.name) // Assuming guestPolicy is an enum, use its name
                    putBoolean("securityCameras", SecurityMeasures.SECURITY_CAMERAS in habitation.securityMeasures)
                    putBoolean("securityGuard", SecurityMeasures.SECURITY_GUARD in habitation.securityMeasures)
                    putBoolean("cardedEntrance", SecurityMeasures.CARDED_ENTRANCE in habitation.securityMeasures)
                    putBoolean("codedEntrance", SecurityMeasures.CODED_ENTRANCE in habitation.securityMeasures)



                }

                findNavController().navigate(

                    R.id.action_habitationFragment_to_editHabitationFragment,
                    bundle
                )
            }


            override fun onDeleteHabitationClick(habitation: Habitation) {
                viewModel.deleteHabitation(habitation.habitationId)
            }
        }
    }
}
