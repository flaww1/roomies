package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.CardItem

class HomeFragment : Fragment() {

    private lateinit var cardStackView: CardStackView
    private lateinit var cardStackManager: CardStackLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        loadCardStack()
    }

    private fun initView(view: View) {
        cardStackView = view.findViewById(R.id.card_stack_view)
        cardStackView.layoutManager = CardStackLayoutManager(requireContext())
        (cardStackView.layoutManager as CardStackLayoutManager).setVisibleCount(3)
    }

    private fun loadCardStack() {
        val cardItems = listOf(
            CardItem("Card 1", "Description 1"),
            CardItem("Card 2", "Description 2"),
            // Add more card items as needed
        )

        val cardAdapter = CardAdapter(cardItems)
        cardStackView.adapter = cardAdapter
    }
}
