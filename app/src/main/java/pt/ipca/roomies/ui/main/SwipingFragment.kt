package pt.ipca.roomies.ui.main

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.CardModel

class SwipingFragment<Room : Any?> : Fragment() {

    private lateinit var cardStackAdapter: CardStackAdapter
    private val roomData: ArrayList<Room> = ArrayList() // Change to Room instead of CardModel
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fetch data from Firestore
        fetchRoomsFromFirestore()

        // Initialize your CardAdapter
        cardStackAdapter = CardStackAdapter(roomData, requireContext())

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        // Initialize your CardAdapter and populate cardData
        cardStackAdapter = CardStackAdapter(roomData, requireContext())

        recyclerView.adapter = cardStackAdapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Handle the swipe event
                onCardSwiped(roomData[viewHolder.adapterPosition], direction)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.your_color))
                    .addSwipeLeftActionIcon(R.drawable.your_icon)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun onCardSwiped(card: CardModel, direction: Int) {
        // Remove the swiped card from cardData
        cardData.remove(card)

        // Check for a match and update Firestore if needed
        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
            // Update Firestore with the swiped card information (e.g., mark it as liked/disliked)
            updateFirestoreOnSwipe(card, direction)
        }

        // Notify the adapter that the data set has changed
        cardStackAdapter.notifyDataSetChanged()
    }

    private fun updateFirestoreOnSwipe(card: CardModel, direction: Int) {
        // Update Firestore with the swiped card information
        val collectionName = if (direction == ItemTouchHelper.LEFT) "dislikedRooms" else "likedRooms"

        db.collection(collectionName).document(card.roomId).set(card)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

    private fun fetchRoomsFromFirestore() {
        db.collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Convert Firestore document to your CardModel
                    val room = document.toObject(CardModel::class.java)
                    roomData.add(room)
                }

                // Notify the adapter that the data set has changed
                cardStackAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
