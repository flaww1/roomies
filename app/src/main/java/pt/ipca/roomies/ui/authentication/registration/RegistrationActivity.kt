package pt.ipca.roomies.ui.authentication.registration

import RegistrationViewModel
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.roomies.databinding.ActivityRegistrationBinding
import pt.ipca.roomies.R

class RegistrationActivity : AppCompatActivity() {

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the initial registration fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegistrationFragment())
                .commit()
        }
    }
}
