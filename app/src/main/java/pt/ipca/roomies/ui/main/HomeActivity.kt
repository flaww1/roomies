package pt.ipca.roomies.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ipca.roomies.R

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottomNavigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the BottomNavigationView with NavController
        bottomNavigationView.setupWithNavController(navController)

        // Optionally, add an OnNavigationItemSelectedListener if needed
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle home item selection
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_messages -> {
                    // Handle messages item selection
                    // navController.navigate(R.id.messagesFragment)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle profile item selection
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
    }
}
