// MainActivity.kt
package pt.ipca.roomies


import LoginFragment
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.roomies.databinding.ActivityMainBinding
import pt.ipca.roomies.ui.authentication.registration.registrationsteps.RegistrationUserProfileInfoFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the initial page with welcome message and buttons
        viewModel.welcomeMessage.observe(this, { message ->
            binding.tvWelcomeMessage.text = message
        })

        binding.btnRegister.setOnClickListener {
            // Navigate to User Profile Info Fragment when Register button is clicked
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegistrationUserProfileInfoFragment())
                .addToBackStack(null)  // Enable back navigation
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            // Navigate to Login Fragment when Login button is clicked
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .addToBackStack(null)  // Enable back navigation
                .commit()
        }
    }
}
