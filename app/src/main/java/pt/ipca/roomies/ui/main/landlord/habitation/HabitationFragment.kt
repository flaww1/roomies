package pt.ipca.roomies.ui.main.landlord.habitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationRepository
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory
import pt.ipca.roomies.data.repositories.RegistrationRepository
import pt.ipca.roomies.data.repositories.RegistrationViewModelFactory
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel


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
                val selectedHabitationId = habitation.habitationId
                if (selectedHabitationId != null) {
                    if (selectedHabitationId.isNotBlank()) {
                        Log.d(
                            "HabitationFragment",
                            "Selected Habitation Before setSelectedHabitationId: $selectedHabitationId"
                        )
                        viewModel.viewModelScope.launch {
                            viewModel.setSelectedHabitationId(selectedHabitationId)
                        }
                        val selectedHabitation = viewModel.selectedHabitation.value
                        Log.d(
                            "HabitationFragment",
                            "Selected Habitation After setSelectedHabitationId: $selectedHabitation"
                        )
                    }
                }

                viewModel.selectHabitation(habitation)
                Log.d("HabitationFragment", "Selected Habitation after selectHabitation: $habitation")
                findNavController().navigate(R.id.action_habitationFragment_to_roomFragment)
            }

            override fun onEditHabitationClick(habitation: Habitation) {
                // Navigate to the habitation editing screen
                viewModel.selectHabitation(habitation)
                //findNavController().navigate(R.id.action_habitationFragment_to_editHabitationFragment)
            }

            override fun onDeleteHabitationClick(habitation: Habitation) {
                viewModel.deleteHabitation(habitation.habitationId!!)
            }
        }
    }
}
