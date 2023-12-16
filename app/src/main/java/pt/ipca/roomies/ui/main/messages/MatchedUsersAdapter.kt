package pt.ipca.roomies.ui.main.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Match

class MatchedUsersAdapter(private val onItemClick: (Match) -> Unit) :
    ListAdapter<Match, MatchedUsersAdapter.MatchedUserViewHolder>(MatchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchedUserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_matched_user, parent, false)
        return MatchedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchedUserViewHolder, position: Int) {
        val matchedUser = getItem(position)
        holder.bind(matchedUser, onItemClick)
    }

    fun submitList(matchedUsers: Set<Match>?) {
        super.submitList(matchedUsers?.toList())

    }

    class MatchedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.textViewMatchedUserName)

        fun bind(matchedUser: Match, onItemClick: (Match) -> Unit) {
            userNameTextView.text = matchedUser.targetUserId // Assuming you have a property like targetUserId in your Match class

            itemView.setOnClickListener {
                onItemClick.invoke(matchedUser)
            }
        }
    }

    private class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.matchId == newItem.matchId
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}
