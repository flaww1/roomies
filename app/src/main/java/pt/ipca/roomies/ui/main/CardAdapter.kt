package pt.ipca.roomies.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.ui.main.card.UserCardFragment
import pt.ipca.roomies.ui.main.card.RoomCardFragment

class CardAdapter(fragment: Fragment, homeViewModel: HomeViewModel) :
    FragmentStateAdapter(fragment) {

    private var cardList: List<Card> = emptyList()

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (val card = cardList[position]) {
            is Card.RoomCard -> {
                Log.d("CardAdapter", "Creating RoomCardFragment for position $position")
                RoomCardFragment.newInstance(card.room)
            }
            is Card.UserCard -> {
                Log.d("CardAdapter", "Creating UserCardFragment for position $position")
                UserCardFragment.newInstance(card.user)
            }
        }
    }

    fun setCardList(cards: List<Card>) {
        this.cardList = cards
        notifyItemChanged(0, cards.size)
    }

    fun setCurrentCard(card: Card?) {
        val position = cardList.indexOf(card)
        if (position != -1) {
            notifyItemChanged(position)
            Log.d("CardAdapter", "Setting current card at position $position")
        }
    }

    // In your CardAdapter


}
