package pt.ipca.roomies.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pt.ipca.roomies.R

class CardFragment : Fragment() {

    private lateinit var btnLike: Button
    private lateinit var btnDislike: Button
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card, container, false)
        btnLike = view.findViewById(R.id.buttonLike)
        btnDislike = view.findViewById(R.id.buttonDislike)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Ensure that arguments are not null before accessing
        val cardContent = arguments?.getString(ARG_CARD_CONTENT)
        if (!cardContent.isNullOrEmpty()) {
            view.findViewById<TextView>(R.id.textViewCardContent).text = cardContent
        }

        // Simplified click listeners using lambda syntax
        btnLike.setOnClickListener { homeViewModel.likeCurrentCard() }

        btnDislike.setOnClickListener { homeViewModel.dislikeCurrentCard() }
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
