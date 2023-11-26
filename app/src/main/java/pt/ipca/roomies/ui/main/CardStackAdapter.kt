package pt.ipca.roomies.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.ipca.roomies.data.entities.CardModel

class CardStackAdapter(
    private var spots: List<CardModel> = emptyList(),
    requireContext: Context
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Initialize your views here
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        // Bind your data to your views here
    }

    fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<CardModel>) {
        this.spots = spots
    }

    fun getSpots(): List<CardModel> {
        return spots
    }
}

