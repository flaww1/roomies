package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import pt.ipca.roomies.R
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.repositories.HomeRepository
import pt.ipca.roomies.data.repositories.HomeViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cardAdapter: CardAdapter
    private lateinit var navController: NavController
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRepository: HomeRepository
    private lateinit var cardRepository: CardRepository

    /*
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Initialize NavController
            navController = findNavController()
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {


            val view = inflater.inflate(R.layout.fragment_home, container, false)

            // Initialize pt.ipca.roomies.ui.main.HomeViewModel
            homeViewModel = ViewModelProvider(this, HomeViewModelFactory(cardRepository))[HomeViewModel::class.java]

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

            // Example of handling button clicks in the fragment
            view.findViewById<Button>(R.id.buttonLike).setOnClickListener {
                homeViewModel.likeCurrentCard()
            }

            view.findViewById<Button>(R.id.buttonDislike).setOnClickListener {
                homeViewModel.dislikeCurrentCard()
            }
        }

        private suspend fun initObservers() {
            // Observe changes in the current card
            homeViewModel.currentCard.observe(viewLifecycleOwner, Observer { card ->
                // Notify the adapter that the data set has changed
                cardAdapter.setCurrentCard(card)
            })
            val userRole = homeRepository.getCurrentUserRole()

            // Fetch the initial card data
            if (userRole != null) {
                homeViewModel.loadNextCard(userRole)
            }
            else {
                Log.d("HomeFragment", "userRole is null")
            }
        }


    }
    */
}