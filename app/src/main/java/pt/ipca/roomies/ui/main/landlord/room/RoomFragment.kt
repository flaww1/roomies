package pt.ipca.roomies.ui.main.landlord.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Room

class RoomFragment : Fragment() {

    private lateinit var viewModel: RoomViewModel
    private lateinit var roomRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)

        roomRecyclerView = view.findViewById(R.id.roomRecyclerView)
        roomRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.rooms.observe(viewLifecycleOwner, Observer { rooms ->
            roomRecyclerView.adapter = RoomAdapter(rooms, object : RoomAdapter.OnRoomClickListener {
                override fun onRoomClick(room: Room) {
                    // Navigate to a new fragment or activity that displays the details of the clicked room
                    viewModel.selectRoom(room)
                }

                override fun onDeleteRoomClick(room: Room) {
                    viewModel.deleteRoom(room.roomId)  // Assuming "id" is the document ID
                }
            })
        })

        viewModel.roomCreationSuccess.observe(viewLifecycleOwner, Observer { documentId ->
            documentId?.let {
                // The room has been created successfully, use the documentId if needed
                viewModel.refreshRooms()
            }
        })

        viewModel.roomDeletionSuccess.observe(viewLifecycleOwner, Observer { deletionSuccess ->
            if (deletionSuccess == true) {
                // The room has been deleted successfully, use this information as needed
                viewModel.refreshRooms()
            }
        })
    }
}