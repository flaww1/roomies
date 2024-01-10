package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.dao.LikeMatchDao
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserDao
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.repositories.HomeRepository
import pt.ipca.roomies.data.repositories.HomeViewModelFactory
import pt.ipca.roomies.data.repositories.LoginRepository

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cardAdapter: CardAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRepository: HomeRepository
    private lateinit var cardRepository: CardRepository
    private lateinit var loginRepository: LoginRepository
    private lateinit var likeMatchDao: LikeMatchDao
    private lateinit var roomDao: RoomDao
    private lateinit var userDao: UserDao

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        likeMatchDao = AppDatabase.getDatabase(requireContext()).likeMatchDao()
        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        userDao = AppDatabase.getDatabase(requireContext()).userDao()

        // Initialize HomeRepository with AppDatabase
        homeRepository = HomeRepository(AppDatabase.getDatabase(requireContext()))

        // Initialize CardRepository
        cardRepository = CardRepository(likeMatchDao, roomDao, userDao)
        loginRepository = LoginRepository(userDao)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize HomeViewModel with CardRepository
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(cardRepository, loginRepository))[HomeViewModel::class.java]

        // Initialize views and adapters
        initView(view)

        // Launch a coroutine to call suspend function initObservers
        lifecycleScope.launch {
            initObservers()
        }

        return view
    }


    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        cardAdapter = CardAdapter(this, homeViewModel)
        viewPager.adapter = cardAdapter

        // Optionally, customize ViewPager2 behavior
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private suspend fun initObservers() {

        val userRole = homeRepository.getCurrentUserRole()

        if (userRole != null) {
            homeViewModel.loadNextCard(userRole)
        }

        homeViewModel.cardList.observe(viewLifecycleOwner, Observer { cards ->
            cards?.let {
                // Update UI based on the card list if needed
                updateUI(cards)
            }
        })

    }
    private fun updateUI(cards: List<Card>) {
        // Assuming cardAdapter is an instance of your pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.CardAdapter
        cardAdapter.setCardList(cards)

        // Assuming you have a TextView to display card information
        if (cards.isNotEmpty()) {
            val currentCard = cards[0]
            // Update UI elements based on the current card
            when (currentCard) {
                is Card.RoomCard -> {
                    Log.d("pt.ipca.roomies.ui.main.HomeFragment", "Displaying RoomCard: ${currentCard.room.roomId}")
                    // Update TextView or other UI elements with room card information
                }
                is Card.UserCard -> {
                    Log.d("pt.ipca.roomies.ui.main.HomeFragment", "Displaying UserCard: ${currentCard.user.userId}")
                    // Update TextView or other UI elements with user card information
                }
            }
        } else {
            // Handle the case where there are no cards to display
            Log.d("pt.ipca.roomies.ui.main.HomeFragment", "No cards to display")
        }
    }




}