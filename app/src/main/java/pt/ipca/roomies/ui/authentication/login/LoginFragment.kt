package pt.ipca.roomies.ui.authentication.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.repositories.LoginRepository
import pt.ipca.roomies.data.repositories.LoginViewModelFactory
import pt.ipca.roomies.databinding.FragmentLoginBinding
import pt.ipca.roomies.ui.authentication.UserViewModel

//classe trabalha com o loginviewwmodel, permite navegação, intereacoes da interface assocciadas com o login, obsertva os resiltados e atualiza

class LoginFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView( //esta função está a criar a interface do utilizador do fragmento a partir de um arquivo de layout XML específico (FragmentLoginBinding). O resultado é armazenado na variável _binding.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginRepository(AppDatabase.getDatabase(requireContext()).userDao()))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.HomeFragment", "pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.pt.ipca.roomies.ui.main.HomeFragment onViewCreated")

        super.onViewCreated(view, savedInstanceState)
        userViewModel = activity?.run { //iniciacao de componentes do user, ouvinte de ckick, chama loginUser
            ViewModelProvider(this)[UserViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        // After the user logs in successfully
        userViewModel.isLoggedIn.value = true
        // Assuming you have a login button in your layout
        binding.buttonLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Call the loginUser function in the ViewModel
            viewModel.loginUser(email, password)
        }

        // Observe the login result
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            Log.d("LoginFragment", "Login result observed: $result")
            when (result) {
                is LoginViewModel.LoginResult.Success -> {

                    Log.d("LoginFragment", "Navigating to the next fragment")
                    navigateToHomeFragment()
                }
                is LoginViewModel.LoginResult.Error -> {
                    // Handle login error, show a message to the user, etc.
                    val errorMessage = result.message
                    // Example: Show an error message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Handle other cases

                }
            }
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }


    override fun onDestroyView() { //final ciclo de vida, destroi e limpa a referencia de visualização
        super.onDestroyView()
        _binding = null
    }
}
