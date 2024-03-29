package pt.ipca.roomies.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.ui.main.card.RoomCardFragment
import pt.ipca.roomies.ui.main.card.UserCardFragment

class CardAdapter(fragment: Fragment, homeViewModel: HomeViewModel) :
    FragmentStateAdapter(fragment) {

    var cardList: List<Card> = emptyList()
        private set

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (val card = cardList[position]) {
            is Card.RoomCard -> {
                Log.d("pt.ipca.roomies.ui.main.CardAdapter", "Creating RoomCardFragment for position $position")
                RoomCardFragment.newInstance(card.room)
            }
            is Card.UserCard -> {
                Log.d("pt.ipca.roomies.ui.main.CardAdapter", "Creating UserCardFragment for position $position")
                UserCardFragment.newInstance(card.user)
            }
        }
    }

    fun setCardList(cards: List<Card>) {
        this.cardList = cards
        notifyItemRangeChanged(0, cards.size)
    }

    fun setCurrentCard(card: Card?) {
        val position = cardList.indexOf(card)
        if (position != -1) {
            notifyItemChanged(position)
            Log.d("pt.ipca.roomies.ui.main.CardAdapter", "Setting current card at position $position")
        }
    }
}
