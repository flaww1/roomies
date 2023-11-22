import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.databinding.FragmentRegistrationUserInterestsBinding
import pt.ipca.roomies.ui.authentication.registration.registrationsteps.RegistrationUserInterestsViewModel
import android.os.Bundle
import android.view.ViewGroup
import android.view.View
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipca.roomies.R


class RegistrationUserInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationUserInterestsBinding
    private lateinit var viewModel: RegistrationUserInterestsViewModel
    private lateinit var adapter: InterestTagsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationUserInterestsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(RegistrationUserInterestsViewModel::class.java)

        // Initialize Adapter here
        adapter = InterestTagsAdapter(listOf()) { tag, isSelected ->
            viewModel.updateSelectedInterestTags(tag, isSelected)
        }
        binding.recyclerViewInterests.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewInterests.layoutManager = LinearLayoutManager(requireContext())
        // Implement UI interactions
        binding.nextButton.setOnClickListener {
            // Navigate to the UserProfileInfoFragment
            navigateToHomePage()
        }

        // Implement back button functionality
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }
        // Observe changes in the selected interest tags
        viewModel.getSelectedInterestTags().observe(viewLifecycleOwner) { selectedTags ->
            // Handle changes, if needed

            // For example, update UI to reflect selected tags
        }

        // Populate the adapter with data
        adapter.updateData(viewModel.getAvailableInterestTags())
    }
    private fun navigateToHomePage() {
        // Use NavController to navigate to the next fragment
        findNavController().navigate(R.id.action_roleSelectionFragment_to_registrationUserProfileInfoFragment)
    }
}
