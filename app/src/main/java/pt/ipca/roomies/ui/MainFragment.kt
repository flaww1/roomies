package pt.ipca.roomies.ui
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import pt.ipca.roomies.R
import pt.ipca.roomies.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        val navController = findNavController()

        binding.btnRegister.setOnClickListener {
            Log.d("MainFragment", "Register button clicked")
            navigateToRegistrationFragment()
        }

        binding.btnLogin.setOnClickListener {
            Log.d("MainFragment", "Login button clicked")

            navigateToLoginFragment()        }



        return view
    }
    private fun navigateToRegistrationFragment() {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.registrationFragment) {
            navController.navigate(R.id.action_mainFragment_to_registrationFragment)
        }
    }

    private fun navigateToLoginFragment() {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.loginFragment) {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }





}
