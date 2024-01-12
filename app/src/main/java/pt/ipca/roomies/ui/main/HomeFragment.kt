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

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val appDatabase = AppDatabase.getDatabase(requireContext())
        val likeMatchDao = appDatabase.likeMatchDao()
        val roomDao = appDatabase.roomDao()
        val userDao = appDatabase.userDao()
        val habitationDao = appDatabase.habitationDao()

        homeRepository = HomeRepository(appDatabase)
        cardRepository = CardRepository(likeMatchDao, roomDao, habitationDao,userDao)
        loginRepository = LoginRepository(userDao)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(cardRepository, loginRepository))[HomeViewModel::class.java]

        initView(view)

        lifecycleScope.launch {
            initObservers()
        }

        return view
    }

    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        cardAdapter = CardAdapter(this, homeViewModel)
        viewPager.adapter = cardAdapter

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        homeViewModel.cardProcessed.observe(viewLifecycleOwner, Observer { isProcessed ->
            if (isProcessed) {
                // Since 'cardProcessed' is a MutableLiveData, we can call 'setValue'
                homeViewModel.cardProcessed.observe(viewLifecycleOwner, Observer { isProcessed ->
                    if (isProcessed) {
                        homeViewModel.resetCardProcessed() // Reset the cardProcessed value using ViewModel method

                        lifecycleScope.launch {
                            val userRole = homeRepository.getCurrentUserRole()
                            if (userRole == "User") {
                                Log.d("HomeFragment", "Loading next room card")
                                homeViewModel.loadNextRoomCard()
                            } else {
                                Log.d("HomeFragment", "Loading next user card")
                                homeViewModel.loadNextUserCard()
                            }
                        }
                    }
                })
            }
        })

    }

    private suspend fun initObservers() {
        homeViewModel.currentCard.observe(viewLifecycleOwner, Observer { card ->
            card?.let {
                cardAdapter.setCardList(listOf(it))
            }
        })

        homeViewModel.nextCard.observe(viewLifecycleOwner, Observer { card ->
            card?.let {
                cardAdapter.setCardList(cardAdapter.cardList + it)
            }
        })

        val userRole = homeRepository.getCurrentUserRole()
        if (userRole == "User") {
            homeViewModel.loadNextRoomCard()
        } else {
            homeViewModel.loadNextUserCard()
        }
    }
}
