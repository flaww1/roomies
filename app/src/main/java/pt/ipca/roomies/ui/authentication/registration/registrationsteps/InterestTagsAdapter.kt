import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.databinding.ItemInterestBinding


class InterestTagsAdapter(
    private var interestTags: List<String>,
    private val onTagClickListener: (String, Boolean) -> Unit
) : RecyclerView.Adapter<InterestTagsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = interestTags[position]
        holder.bind(tag)

        holder.itemView.setOnClickListener {
            holder.toggleSelection()
            onTagClickListener.invoke(tag, holder.isSelected)
        }
    }
    fun updateData(newInterestTags: List<String>) {
        this.interestTags = newInterestTags
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = interestTags.size

    inner class ViewHolder(private val binding: ItemInterestBinding) : RecyclerView.ViewHolder(binding.root) {

        var isSelected = false
            private set

        fun bind(tag: String) {
            binding.textInterestTag.text = tag
        }

        fun toggleSelection() {
            isSelected = !isSelected
            binding.root.isSelected = isSelected
        }
    }


}

