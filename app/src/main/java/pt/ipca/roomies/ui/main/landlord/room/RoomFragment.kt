package pt.ipca.roomies.ui.main.landlord.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.HabitationDao
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserProfileDao
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.HabitationViewModelFactory
import pt.ipca.roomies.data.repositories.RoomRepository
import pt.ipca.roomies.data.repositories.RoomViewModelFactory
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class RoomFragment : Fragment() {

    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomDao: RoomDao
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var roomRepository: RoomRepository
    private lateinit var habitationDao: HabitationDao

    private val roomViewModel: RoomViewModel by viewModels {
        RoomViewModelFactory(roomRepository)
    }

    private val habitationViewModel: HabitationViewModel by viewModels {
        HabitationViewModelFactory(habitationDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
        roomRepository = RoomRepository(userProfileDao, roomDao)
        habitationDao = AppDatabase.getDatabase(requireContext()).habitationDao()

        roomRecyclerView = view.findViewById(R.id.roomRecyclerView)
        roomRecyclerView.layoutManager = LinearLayoutManager(context)

        habitationViewModel.selectedHabitation.observe(viewLifecycleOwner) { selectedHabitation ->
            selectedHabitation?.let { habitation ->
                habitation.habitationId?.let {
                    roomViewModel.getRoomsByHabitationId(it)
                }
            }
        }

        roomViewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            roomRecyclerView.adapter = RoomAdapter(
                rooms,
                object : RoomAdapter.OnRoomClickListener {
                    override fun onRoomClick(room: Room) {
                        roomViewModel.selectRoom(room)
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
                            room.roomId?.let { roomId ->
                                roomViewModel.deleteRoom(habitationId, roomId)
                            }
                        }
                    }

                    override fun onEditRoomClick(room: Room) {
                        roomViewModel.selectRoom(room)
                    }
                }
            )
        }

        val fabCreateRoom: FloatingActionButton = view.findViewById(R.id.fabCreateRoom)
        fabCreateRoom.setOnClickListener {
            val selectedHabitation = habitationViewModel.selectedHabitation.value
            if (selectedHabitation == null || selectedHabitation.habitationId.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "No habitation selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                val bundle = Bundle().apply {
                    putString("habitationId", selectedHabitation.habitationId)
                }
                findNavController().navigate(R.id.action_roomFragment_to_createRoomFragment, bundle)
            }
        }
    }
}
