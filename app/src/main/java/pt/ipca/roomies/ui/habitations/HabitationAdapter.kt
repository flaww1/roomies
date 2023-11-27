package pt.ipca.roomies.ui.habitations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Habitation

class HabitationAdapter : RecyclerView.Adapter<HabitationAdapter.ViewHolder>() {

    // Data source for habitations
    private var habitations: List<Habitation> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Initialize your views here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_habitation, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habitation = habitations[position]
        // Bind your data to your views here
    }

    override fun getItemCount(): Int {
        return habitations.size
    }

    fun setHabitations(habitations: List<Habitation>) {
        this.habitations = habitations
        notifyDataSetChanged()
    }
}