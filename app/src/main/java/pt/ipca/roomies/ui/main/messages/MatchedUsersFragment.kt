package pt.ipca.roomies.ui.main.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Match

class MatchedUsersFragment : Fragment() {

    private lateinit var viewModel: MessagesViewModel
    private lateinit var matchedUsersAdapter: MatchedUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matched_users, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MessagesViewModel::class.java)
        matchedUsersAdapter = MatchedUsersAdapter { matchedUser -> onMatchedUserClicked(matchedUser) }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMatchedUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = matchedUsersAdapter

        viewModel.matches.observe(viewLifecycleOwner, Observer { matches ->
            matchedUsersAdapter.submitList(matches.toList())
        })

        return view
    }

    private fun onMatchedUserClicked(matchedUser: Match) {
        // Handle click on a matched user, navigate to the MessageFragment
       // requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
    }
}
