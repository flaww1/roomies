package pt.ipca.roomies.ui.main.landlord.habitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import pt.ipca.roomies.ui.main.landlord.SharedHabitationViewModel

class HabitationFragment : Fragment() {

    private lateinit var viewModel: HabitationViewModel
    private lateinit var habitationRecyclerView: RecyclerView
    val sharedHabitationViewModel: SharedHabitationViewModel by activityViewModels()


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

        val fabCreateHabitation: FloatingActionButton = view.findViewById(R.id.fabCreateHabitation)
        fabCreateHabitation.setOnClickListener {
            // Create a new habitation
            val newHabitation = Habitation(/* populate with your habitation details */)
            viewModel.createHabitation(newHabitation)
        }

        viewModel.habitations.observe(viewLifecycleOwner, Observer { habitations ->
            habitationRecyclerView.adapter = HabitationAdapter(habitations, object : HabitationAdapter.OnHabitationClickListener {
                override fun onHabitationClick(habitation: Habitation) {
                    // Set the document ID of the selected habitation
                    habitation.habitationId?.let { nonNullId ->
                        viewModel.setHabitationDocumentId(nonNullId)
                    }
                    // Navigate to a new fragment or activity that displays the details of the clicked habitation
                    sharedHabitationViewModel.selectHabitation(habitation, habitation.habitationId.toString())
                    val navController = findNavController()
                    navController.navigate(R.id.action_habitationFragment_to_roomFragment)
                }

                override fun onEditHabitationClick(habitation: Habitation) {
                    // Navigate to a new fragment or activity that allows editing the clicked habitation
                    val navController = findNavController()
                    //navController.navigate(R.id.action_habitationFragment_to_editHabitationFragment)
                }

                override fun onDeleteHabitationClick(habitation: Habitation) {
                    habitation.habitationId?.let { nonNullId ->
                        viewModel.deleteHabitation(nonNullId)
                    }
                }
            })
        })

        viewModel.habitationCreationSuccess.observe(viewLifecycleOwner, Observer { documentId ->
            documentId?.let {
                // The habitation has been created successfully, use the documentId if needed
                viewModel.refreshHabitations()
            }
        })

        viewModel.habitationDeletionSuccess.observe(viewLifecycleOwner, Observer { deletionSuccess ->
            if (deletionSuccess == true) {
                // The habitation has been deleted successfully, use this information as needed
                viewModel.refreshHabitations()
            }
        })
    }
}





