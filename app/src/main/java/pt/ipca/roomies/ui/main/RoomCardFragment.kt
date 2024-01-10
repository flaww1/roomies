package pt.ipca.roomies.ui.main.card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.LikeMatchDao
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserDao
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.repositories.HomeViewModelFactory
import pt.ipca.roomies.data.repositories.LoginRepository
import pt.ipca.roomies.ui.main.HomeViewModel

class RoomCardFragment : Fragment() {

    private lateinit var room: Room
    private lateinit var btnLike: Button
    private lateinit var btnDislike: Button

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cardRepository: CardRepository
    private lateinit var loginRepository: LoginRepository
    private lateinit var likeMatchDao: LikeMatchDao
    private lateinit var roomDao: RoomDao
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize room from arguments
        arguments?.let {
            room = it.getParcelable(ARG_ROOM) ?: Room()
        }

        // Initialize DAOs
        likeMatchDao = AppDatabase.getDatabase(requireContext()).likeMatchDao()
        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        userDao = AppDatabase.getDatabase(requireContext()).userDao()

        // Initialize repositories
        cardRepository = CardRepository(likeMatchDao, roomDao, userDao)
        loginRepository = LoginRepository(userDao)

        // Initialize homeViewModel
        homeViewModel = ViewModelProvider(requireActivity(), HomeViewModelFactory(cardRepository, loginRepository))[HomeViewModel::class.java]

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_card, container, false)
        btnLike = view.findViewById(R.id.buttonLike)
        btnDislike = view.findViewById(R.id.buttonDislike)

        view.findViewById<TextView>(R.id.roomDescriptionTextView).text = room.description

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initially disable the buttons until a card is loaded
        btnLike.isEnabled = false
        btnDislike.isEnabled = false

        // Observe changes in the current card
        homeViewModel.currentCard.observe(viewLifecycleOwner) { card ->
            if (card != null) {
                // Enable the buttons because we have a card loaded
                btnLike.isEnabled = true
                btnDislike.isEnabled = true
            } else {
                // Disable the buttons if there is no card loaded
                btnLike.isEnabled = false
                btnDislike.isEnabled = false
            }
        }


        btnLike.setOnClickListener {
            Log.d("UserCardFragment", "Like button clicked")
            try {
                homeViewModel.likeCurrentCard()
            } catch (e: Exception) {
                Log.e("pt.ipca.roomies.ui.main.HomeFragment", "Exception in likeCurrentCard: $e")
            }
        }
        btnDislike.setOnClickListener { homeViewModel.dislikeCurrentCard() }
    }

    companion object {
        private const val ARG_ROOM = "argRoom"

        fun newInstance(room: Room): RoomCardFragment {
            val fragment = RoomCardFragment()
            val args = Bundle()
            args.putParcelable(ARG_ROOM, room)
            fragment.arguments = args
            return fragment
        }
    }
}
