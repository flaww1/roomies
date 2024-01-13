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

class MatchedUsersAdapter(private val onItemClick: (Match) -> Unit) :   //lista de matches para a interface do utilizador
    ListAdapter<Match, MatchedUsersAdapter.MatchedUserViewHolder>(MatchDiffCallback()) {  //  adaptador é usado para exibir uma lista de usuários correspondentes RecyclerView, e a classe MatchedUserViewHolder lida com a exibição e atualização dos itens individuais. click origina a acao apropriada

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchedUserViewHolder { //Preenche a visualização criada em onCreateViewHolder com os dados de um usuário correspondente específico. 
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_matched_user, parent, false)
        return MatchedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchedUserViewHolder, position: Int) { //Informa ao adaptador qual lista de users correspondentes deve ser exibida.
        val matchedUser = getItem(position)
        holder.bind(matchedUser, onItemClick)
    }

    fun submitList(matchedUsers: Set<Match>?) { //informa adaptador qual lista de users correspondentes deve ser exibida. Quando a lista é atualizada, esta função é chamada para mostrar os novos users correspondentes.
        super.submitList(matchedUsers?.toList())

    }

    class MatchedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {  /
        private val userNameTextView: TextView = itemView.findViewById(R.id.textViewMatchedUserName)

        fun bind(matchedUser: Match, onItemClick: (Match) -> Unit) {
            userNameTextView.text = matchedUser.targetUserId // Assuming you have a property like targetUserId in your Match class

            itemView.setOnClickListener {
                onItemClick.invoke(matchedUser)
            }
        }
    }

    private class MatchDiffCallback : DiffUtil.ItemCallback<Match>() { //Ajuda a verificar quais itens mudaram na lista para que apenas as alterações necessárias sejam feitas.
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.matchId == newItem.matchId
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}
