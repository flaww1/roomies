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
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.ui.main.HomeViewModel

class RoomCardFragment : Fragment() {

    private lateinit var room: Room
    private lateinit var btnLike: Button
    private lateinit var btnDislike: Button

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_room_card, container, false)
        btnLike = view.findViewById(R.id.buttonLike)
        btnDislike = view.findViewById(R.id.buttonDislike)

        // Display room information in your layout as needed
        view.findViewById<TextView>(R.id.roomDescriptionTextView).text = room.description

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RoomCardFragment", "onViewCreated: $room")

        // Simplified click listeners using lambda syntax
        btnLike.setOnClickListener {
            Log.d("RoomCardFragment", "Like button clicked")
            homeViewModel.likeCurrentCard()
        }

        btnDislike.setOnClickListener {
            Log.d("RoomCardFragment", "Dislike button clicked")
            homeViewModel.dislikeCurrentCard()
        }
    }

    companion object {
        private const val ARG_ROOM = "argRoom"

        fun newInstance(room: Room): RoomCardFragment {
            val fragment = RoomCardFragment()
            val args = Bundle()
            args.putParcelable(ARG_ROOM, room)
            fragment.arguments = args
            return fragment
        }
    }
}
