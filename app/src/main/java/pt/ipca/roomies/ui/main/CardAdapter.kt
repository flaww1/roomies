package pt.ipca.roomies.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.ui.main.card.RoomCardFragment
import pt.ipca.roomies.ui.main.card.UserCardFragment

class CardAdapter(fragment: Fragment, private val homeViewModel: HomeViewModel) :
    FragmentStateAdapter(fragment) {

    private var cardList: List<Card> = emptyList()

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun createFragment(position: Int): Fragment {
        val card = cardList[position]
        return when (card) {
            is Card.RoomCard -> RoomCardFragment.newInstance(card.room)
            is Card.UserCard -> UserCardFragment.newInstance(card.user)
        }
    }

    fun setCardList(cards: List<Card>) {
        this.cardList = cards
        notifyItemChanged(0, cardList.size)
    }
}
