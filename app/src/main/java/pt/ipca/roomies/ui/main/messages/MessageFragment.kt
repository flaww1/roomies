package pt.ipca.roomies.ui.main.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Match

class MessageFragment : Fragment() {

    private lateinit var recyclerViewMatchedUsers: RecyclerView
    private lateinit var matchedUsersAdapter: MatchedUsersAdapter
    private lateinit var messagesViewModel: MessagesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        recyclerViewMatchedUsers = view.findViewById(R.id.recyclerViewMatchedUsers)
        matchedUsersAdapter = MatchedUsersAdapter { matchedUser ->

            openMessageChatFragment(matchedUser)

        }

        recyclerViewMatchedUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchedUsersAdapter
        }

        messagesViewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)

        messagesViewModel.matchedUsers.observe(viewLifecycleOwner, Observer { matchedUsers ->
            matchedUsersAdapter.submitList(matchedUsers)
        })

        return view
    }

    private fun openMessageChatFragment(matchedUser: Match) {
        val args = Bundle()
        // Pass any necessary data to the MessageChatFragment using args

        // Create an instance of MessageChatFragment and set arguments
        val messageChatFragment = MessageChatFragment()
        messageChatFragment.arguments = args


        findNavController().navigate(R.id.action_messageFragment_to_messageChatFragment, args)
    }
}
