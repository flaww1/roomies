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

        // Initialize pt.ipca.roomies.ui.main.HomeViewModel
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Customize the card content as needed
        val cardContent = arguments?.getString(ARG_CARD_CONTENT)
        view.findViewById<TextView>(R.id.textViewCardContent).text = cardContent

        btnLike.setOnClickListener {
            // Implement your like functionality here
            homeViewModel.likeCurrentCard()
        }

        btnDislike.setOnClickListener {
            // Implement your dislike functionality here
            homeViewModel.dislikeCurrentCard()
        }
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
