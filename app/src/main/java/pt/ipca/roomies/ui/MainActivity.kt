package pt.ipca.roomies.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.ui.authentication.UserViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)



        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle navigation to home
                    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
                    true
                }
                R.id.navigation_messages -> {
                    // Handle navigation to dashboard
                    // findNavController().navigate(R.id.messagesFragment)
                    true
                }
                R.id.navigation_habitations -> {
                    // Handle navigation to notifications
                    findNavController(R.id.nav_host_fragment).navigate(R.id.habitationFragment)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle navigation to profile
                    findNavController(R.id.nav_host_fragment).navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }





        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )


        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            // Use the navigation controller to navigate to the start destination
            navController.navigate(R.id.mainFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



}







