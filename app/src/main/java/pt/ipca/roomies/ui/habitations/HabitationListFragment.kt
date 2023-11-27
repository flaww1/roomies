package pt.ipca.roomies.ui.habitations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R

// HabitationListFragment.kt
class HabitationListFragment : Fragment() {

    private lateinit var habitationAdapter: HabitationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habitation_list, container, false)
        // Initialize and set up your RecyclerView and adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_habitations)
        habitationAdapter = HabitationAdapter()
        recyclerView.adapter = habitationAdapter
        // Implement logic to fetch and populate habitations in the adapter
        return view
    }

    // Implement any other necessary fragment logic
}