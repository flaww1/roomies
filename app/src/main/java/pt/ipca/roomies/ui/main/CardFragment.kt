package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.roomies.R

class CardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Customize the card content as needed
        val cardContent = arguments?.getString(ARG_CARD_CONTENT)
        view.findViewById<TextView>(R.id.textViewCardContent).text = cardContent
    }

    companion object {
        private const val ARG_CARD_CONTENT = "argCardContent"

        fun newInstance(cardContent: String): CardFragment {
            val fragment = CardFragment()
            val args = Bundle()
            args.putString(ARG_CARD_CONTENT, cardContent)
            fragment.arguments = args
            return fragment
        }
    }
}
