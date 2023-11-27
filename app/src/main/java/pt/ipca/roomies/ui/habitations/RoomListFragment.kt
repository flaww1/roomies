package pt.ipca.roomies.ui.habitations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.ui.main.RoomAdapter

class RoomListFragment : Fragment() {

    private lateinit var roomAdapter: RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_list, container, false)
        // Initialize and set up your RecyclerView and adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_rooms)
        roomAdapter = RoomAdapter()
        recyclerView.adapter = roomAdapter
        // Implement logic to fetch and populate rooms in the adapter
        return view
    }

    // Implement any other necessary fragment logic
}