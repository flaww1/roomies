package pt.ipca.roomies.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipca.roomies.data.entities.Card

class CardAdapter(fragment: Fragment, private val homeViewModel: HomeViewModel) :
    FragmentStateAdapter(fragment) {


    private var currentCard: String? = null

    override fun getItemCount(): Int {
        // For a single card, the count is always 1
        return 1
    }

    override fun createFragment(position: Int): Fragment {
        return CardFragment.newInstance(cardList[position])
    }


    private var cardList: List<String> = emptyList()

    fun setCurrentCard(card: Card?) {
        this.currentCard = card.toString()
        notifyItemChanged(0)
    }
}
