package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ipca.roomies.R

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cardAdapter: CardAdapter
    private val cardContents = listOf("Card 1", "Card 2", "Card 3") // Add your card contents
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize navController
        navController = findNavController()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        bottomNavigationView = view.findViewById(R.id.bottomNavigation)
        cardAdapter = CardAdapter(this, cardContents)
        viewPager.adapter = cardAdapter

        // Set up the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle navigation to home
                    true
                }
                R.id.navigation_messages -> {
                    // Handle navigation to dashboard
                    true
                }
                R.id.navigation_profile -> {
                    // Handle navigation to notifications
                    navController.navigate(R.id.profileFragment)

                    true
                }
                else -> false
            }
        }

        // Optionally, customize ViewPager2 behavior
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}



