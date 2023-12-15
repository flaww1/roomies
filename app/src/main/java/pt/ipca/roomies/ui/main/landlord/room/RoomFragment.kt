package pt.ipca.roomies.ui.main.landlord.room

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class RoomFragment : Fragment() {

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var habitationViewModel: HabitationViewModel
    private lateinit var roomRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomViewModel = ViewModelProvider(requireActivity())[RoomViewModel::class.java]
        habitationViewModel = ViewModelProvider(requireActivity())[HabitationViewModel::class.java]

        roomRecyclerView = view.findViewById(R.id.roomRecyclerView)
        roomRecyclerView.layoutManager = LinearLayoutManager(context)

        // Observe changes in the selectedHabitation directly from the RoomViewModel
        habitationViewModel.selectedHabitation.observe(viewLifecycleOwner) { selectedHabitation ->
            selectedHabitation?.let { habitation ->
                // When the selectedHabitation changes, fetch rooms for the new habitation
                habitation.habitationId.let {
                    if (it != null) {
                        roomViewModel.getRoomsByHabitationId(it)
                    }
                }
            }
        }

        roomViewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            roomRecyclerView.adapter = RoomAdapter(rooms, object : RoomAdapter.OnRoomClickListener {
                override fun onRoomClick(room: Room) {
                    // Navigate to a new fragment or activity that displays the details of the clicked room
                    roomViewModel.selectRoom(room)
                   // findNavController().navigate(R.id.action_roomFragment_to_roomDetailFragment)
                }

                override fun onDeleteRoomClick(room: Room) {
                    val selectedHabitation = habitationViewModel.selectedHabitation.value

                    if (selectedHabitation == null) {
                        Toast.makeText(
                            requireContext(),
                            "No habitation selected",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                        return
                    }

                    selectedHabitation.habitationId?.let { habitationId ->
                        room.roomId?.let { roomViewModel.deleteRoom(habitationId, it) }
                    }
                }
            })
        }

        roomViewModel.roomCreationSuccess.observe(viewLifecycleOwner) { documentId ->
            documentId?.let {
                // The room has been created successfully, use the documentId if needed
                roomViewModel.refreshRooms()
            }
        }

        roomViewModel.roomDeletionSuccess.observe(viewLifecycleOwner) { deletionSuccess ->
            if (deletionSuccess == true) {
                // The room has been deleted successfully, use this information as needed
                roomViewModel.refreshRooms()
            }
        }

        val fabCreateRoom: FloatingActionButton = view.findViewById(R.id.fabCreateRoom)
        fabCreateRoom.setOnClickListener {
            // Navigate to the room creation screen
            val selectedHabitation = habitationViewModel.selectedHabitation.value
            if (selectedHabitation == null || selectedHabitation.habitationId.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "No habitation selected",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener

L            } else {
                findNavController().navigate(R.id.action_roomFragment_to_createRoomFragment)
            }
        }
    }
}
