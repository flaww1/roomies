package pt.ipca.roomies.ui.authentication.registration


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import pt.ipca.roomies.databinding.ActivityRegistrationBinding
import pt.ipca.roomies.R

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the initial registration fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            // Use the navigation controller to navigate to the start destination
            navController.navigate(R.id.registrationFragment)
        }
    }
}
