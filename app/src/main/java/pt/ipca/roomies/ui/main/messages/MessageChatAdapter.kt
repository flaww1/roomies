package pt.ipca.roomies.ui.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Message

class MessageChatAdapter(private val recyclerView: RecyclerView) :
    ListAdapter<Message, MessageChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    fun addMessage(content: String) {
        val message = Message(content = content)

        currentList.toMutableList().apply {
            add(message)
        }.also {
            submitList(it)
        }

        recyclerView.smoothScrollToPosition(itemCount - 1)
    }


    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageContentTextView: TextView = itemView.findViewById(R.id.textViewMessageContent)

        fun bind(message: Message) {
            messageContentTextView.text = message.content
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
