package pt.ipca.roomies.ui.main.card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.User
import pt.ipca.roomies.ui.main.HomeViewModel

class UserCardFragment : Fragment() {

    private lateinit var user: User
    private lateinit var btnLike: Button
    private lateinit var btnDislike: Button

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_card, container, false)
        btnLike = view.findViewById(R.id.buttonLike)
        btnDislike = view.findViewById(R.id.buttonDislike)

        // Display user information in your layout as needed
        view.findViewById<TextView>(R.id.userNameTextView).text = user.fullName

        // Add more fields as needed

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("UserCardFragment", "onViewCreated: $user")

        // Simplified click listeners using lambda syntax
        btnLike.setOnClickListener {
            Log.d("UserCardFragment", "Like button clicked")
            homeViewModel.likeCurrentCard()
        }

        btnDislike.setOnClickListener {
            Log.d("UserCardFragment", "Dislike button clicked")
            homeViewModel.dislikeCurrentCard()
        }
    }

    companion object {
        private const val ARG_USER = "argUser"

        fun newInstance(user: User): UserCardFragment {
            val fragment = UserCardFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}
