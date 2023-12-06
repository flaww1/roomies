package pt.ipca.roomies.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pt.ipca.roomies.ui.main.CardFragment

class CardAdapter(fragment: Fragment, private val cardContents: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return cardContents.size
    }

    override fun createFragment(position: Int): Fragment {
        return CardFragment.newInstance(cardContents[position])
    }
}
