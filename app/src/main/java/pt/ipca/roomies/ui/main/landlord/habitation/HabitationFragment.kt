package pt.ipca.roomies.ui.main.landlord.habitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Habitation

class HabitationFragment : Fragment() {

    private lateinit var viewModel: HabitationViewModel
    private lateinit var habitationRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(this)[HabitationViewModel::class.java]


        habitationRecyclerView = view.findViewById(R.id.habitationRecyclerView)
        habitationRecyclerView.layoutManager = LinearLayoutManager(context)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.getHabitationsByLandlordId(currentUser.uid)
        }

        // Observe the habitations LiveData
        viewModel.habitations.observe(viewLifecycleOwner, Observer { habitations ->
            habitationRecyclerView.adapter = HabitationAdapter(
                habitations,
                object : HabitationAdapter.OnHabitationClickListener {
                    override fun onHabitationClick(habitation: Habitation) {
                        // Navigate to a new fragment or activity that displays the rooms associated with the clicked habitation
                        viewModel.selectHabitation(habitation)
                        findNavController().navigate(R.id.action_habitationFragment_to_roomFragment)
                    }

                    override fun onEditHabitationClick(habitation: Habitation) {
                        // Handle the edit action for the clicked habitation
                        // Example: navigate to the edit screen or show an edit dialog
                    }

                    override fun onDeleteHabitationClick(habitation: Habitation) {
                        // Handle the delete action for the clicked habitation
                        habitation.habitationId?.let { viewModel.deleteHabitation(it) }
                    }


                }
            )
        })

        // Observe the creation success LiveData
        viewModel.habitationCreationSuccess.observe(viewLifecycleOwner, Observer { documentId ->
            documentId?.let {
                // The habitation has been created successfully, use the documentId if needed
                viewModel.refreshHabitations()
            }
        })

        // Observe the deletion success LiveData
        viewModel.habitationDeletionSuccess.observe(viewLifecycleOwner, Observer { deletionSuccess ->
            if (deletionSuccess == true) {
                // The habitation has been deleted successfully, use this information as needed
                viewModel.refreshHabitations()
            }
        })

        val fabCreateHabitation: FloatingActionButton = view.findViewById(R.id.fabCreateHabitation)
        fabCreateHabitation.setOnClickListener {
            // Navigate to the habitation creation screen

            findNavController().navigate(R.id.action_habitationFragment_to_createHabitationFragment)
        }


    }
}