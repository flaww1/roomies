package pt.ipca.roomies.ui.main.landlord.habitation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class HabitationAdapter(
    private val habitations: List<Habitation>,
    private val onHabitationClickListener: OnHabitationClickListener
) : RecyclerView.Adapter<HabitationAdapter.HabitationViewHolder>() {
    class HabitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitationNameTextView: TextView = view.findViewById(R.id.habitationNameTextView)
        val editHabitationButton: Button = view.findViewById(R.id.editHabitationButton)
        val deleteHabitationButton: Button = view.findViewById(R.id.deleteHabitationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habitation_item, parent, false)
        return HabitationViewHolder(view)
    }


    override fun onBindViewHolder(holder: HabitationViewHolder, position: Int) {
        val habitation = habitations[position]
        holder.habitationNameTextView.text = habitation.address

        holder.editHabitationButton.setOnClickListener {
            onHabitationClickListener.onEditHabitationClick(habitation)
        }

        holder.deleteHabitationButton.setOnClickListener {
            onHabitationClickListener.onDeleteHabitationClick(habitation)
        }

        holder.itemView.setOnClickListener {
            onHabitationClickListener.onHabitationClick(habitation)
        }
    }

    override fun getItemCount() = habitations.size

    interface OnHabitationClickListener {
        fun onHabitationClick(habitation: Habitation)
        fun onEditHabitationClick(habitation: Habitation)
        fun onDeleteHabitationClick(habitation: Habitation)
    }
}
