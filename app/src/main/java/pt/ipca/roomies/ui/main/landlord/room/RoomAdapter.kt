package pt.ipca.roomies.ui.main.landlord.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Room

class RoomAdapter(private val rooms: List<Room>, private val onRoomClickListener: OnRoomClickListener) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomNameTextView: TextView = view.findViewById(R.id.roomNameTextView)
        val editRoomButton: Button = view.findViewById(R.id.editRoomButton)
        val deleteRoomButton: Button = view.findViewById(R.id.deleteRoomButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.roomNameTextView.text = room.description

        holder.editRoomButton.setOnClickListener {
            // Implement the logic for editing a room
        }

        holder.deleteRoomButton.setOnClickListener {
            onRoomClickListener.onDeleteRoomClick(room)
        }

        holder.itemView.setOnClickListener {
            onRoomClickListener.onRoomClick(room)
        }
    }

    override fun getItemCount() = rooms.size

    interface OnRoomClickListener {
        fun onRoomClick(room: Room)
        fun onDeleteRoomClick(room: Room)
    }
}
