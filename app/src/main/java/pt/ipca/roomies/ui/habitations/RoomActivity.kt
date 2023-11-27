package pt.ipca.roomies.ui.habitations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Room

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    // Data source for rooms
    private var rooms: List<Room> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Initialize your views here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_room, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        // Bind your data to your views here
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    fun setRooms(rooms: List<Room>) {
        this.rooms = rooms
        notifyDataSetChanged()
    }
}