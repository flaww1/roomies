package pt.ipca.roomies.ui.main.users.habitation

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.R
import pt.ipca.roomies.data.entities.Habitation

class HabitationAdapter(
    private var habitations: MutableList<Habitation>,
    private val onHabitationClickListener: OnHabitationClickListener
) : RecyclerView.Adapter<HabitationAdapter.HabitationViewHolder>() {
    inner class HabitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val habitationNameTextView: TextView = view.findViewById(R.id.habitationNameTextView)
        val editHabitationButton: Button = view.findViewById(R.id.editHabitationButton)
        val deleteHabitationButton: Button = view.findViewById(R.id.deleteHabitationButton)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHabitationClickListener.onHabitationClick(habitations[position])
                }
            }

            editHabitationButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHabitationClickListener.onEditHabitationClick(habitations[position])
                }
            }

            deleteHabitationButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHabitationClickListener.onDeleteHabitationClick(habitations[position])
                }
            }
        }
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
            // Show the confirmation dialog when the delete button is clicked
            showDeleteConfirmationDialog(habitation, holder.itemView.context)
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

    // Inside your HabitationAdapter class
    fun updateData(newList: List<Habitation>) {
        habitations.clear()
        habitations.addAll(newList)
        notifyItemChanged(0, newList.size)
    }
    private fun showDeleteConfirmationDialog(habitation: Habitation, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Habitation")
        builder.setMessage("Are you sure you want to delete this habitation?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Delete the habitation
            onHabitationClickListener.onDeleteHabitationClick(habitation)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.create().show()
    }




}
