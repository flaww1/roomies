package pt.ipca.roomies.ui.main.messages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Message
import pt.ipca.roomies.ui.messages.MessageChatAdapter

class MessageChatFragment : Fragment() {

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var messageChatAdapter: MessageChatAdapter
    private lateinit var inputMessageEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var messagesViewModel: MessagesViewModel

    private val senderUserId = Firebase.auth.currentUser?.uid ?: "defaultSenderId"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message_chat, container, false)

        recyclerViewChat = view.findViewById(R.id.recyclerViewChat)
        inputMessageEditText = view.findViewById(R.id.inputMessageEditText)
        sendButton = view.findViewById(R.id.sendButton)

        messageChatAdapter = MessageChatAdapter(recyclerViewChat)
        recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageChatAdapter
        }

        messagesViewModel = ViewModelProvider(requireActivity())[MessagesViewModel::class.java]

        val targetUserId = arguments?.getString(ARG_TARGET_USER_ID)
        if (targetUserId != null) {
            messagesViewModel.getChatMessages(targetUserId).observe(viewLifecycleOwner, Observer { chatMessages ->
                messageChatAdapter.submitList(chatMessages)
                // Scroll to the bottom when new messages arrive
                recyclerViewChat.smoothScrollToPosition(messageChatAdapter.itemCount - 1)
            })
        }

        sendButton.setOnClickListener {
            val messageText = inputMessageEditText.text?.toString()
            if (!messageText.isNullOrBlank() && targetUserId != null) {
                val message = Message(senderUserId = senderUserId, receiverUserId = targetUserId, content = messageText)
                messagesViewModel.sendMessage(message)
                inputMessageEditText.text?.clear()
            }
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView and its adapter
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat)
        messageChatAdapter = MessageChatAdapter(recyclerViewChat)

        recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageChatAdapter
        }


        // Register a BroadcastReceiver to listen for new messages
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "NEW_MESSAGE") {
                    val sender = intent.getStringExtra("sender")
                    val content = intent.getStringExtra("content")

                    // Update your UI with the new message
                    // For example, append the message to the chat view
                    val message = "$sender: $content"
                    appendMessageToChat(message)
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("NEW_MESSAGE"))
    }


    companion object {
        private const val ARG_TARGET_USER_ID = "argTargetUserId"

        fun newInstance(targetUserId: String): MessageChatFragment {
            val fragment = MessageChatFragment()
            val args = Bundle()
            args.putString(ARG_TARGET_USER_ID, targetUserId)
            fragment.arguments = args
            return fragment
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "NEW_MESSAGE") {
                val sender = intent.getStringExtra("sender")
                val content = intent.getStringExtra("content")

                // Update your UI with the new message
                // For example, append the message to the chat view
                val message = "$sender: $content"
                appendMessageToChat(message)
            }
        }
    }

    private fun appendMessageToChat(message: String) {
        // Implement this method to append the new message to your chat UI
        // For example, if you are using a RecyclerView with an adapter, you can add the message to the adapter's data list
        messageChatAdapter.addMessage(message)
        // Notify the adapter that the data set has changed
        messageChatAdapter.notifyItemChanged(messageChatAdapter.itemCount - 1)
        // Scroll to the bottom when new messages arrive
        recyclerViewChat.smoothScrollToPosition(messageChatAdapter.itemCount - 1)
    }

    override fun onDestroyView() {
        // Unregister the BroadcastReceiver to avoid memory leaks
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroyView()
    }


}
