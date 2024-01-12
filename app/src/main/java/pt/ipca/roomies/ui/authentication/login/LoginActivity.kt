package pt.ipca.roomies.ui.authentication.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.ActivityLoginBinding
import pt.ipca.roomies.ui.authentication.registration.RegistrationFragment

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the initial registration fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController //transicao entre diferentes fragmentos, para outros destinos e partes da interface

        if (savedInstanceState == null) {
            // Use the navigation controller to navigate to the start destination
            navController.navigate(R.id.loginFragment)
        }
    }
}
