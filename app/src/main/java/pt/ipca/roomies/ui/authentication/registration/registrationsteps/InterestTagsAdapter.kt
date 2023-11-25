import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.databinding.ItemInterestBinding

class InterestTagsAdapter(
    private var interestTags: List<ProfileTags>,
    private val onTagClickListener: (ProfileTags) -> Unit,
    private val profileTagsRepository: ProfileTagsRepository,
    private val userId: String
) : RecyclerView.Adapter<InterestTagsAdapter.ViewHolder>() {

    private val selectedTags = mutableSetOf<String>()

    inner class ViewHolder(private val binding: ItemInterestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: ProfileTags) {
            binding.textInterestTag.text = tag.tagName
            binding.checkboxInterest.isChecked = selectedTags.contains(tag.tagId)
        }

        fun toggleSelection() {
            val selectedTag = interestTags[adapterPosition]
            val tagId = selectedTag.tagId

            if (selectedTags.contains(tagId)) {
                selectedTags.remove(tagId)
            } else {
                selectedTags.add(tagId)
            }

            notifyItemChanged(adapterPosition)

            // Update the UserTags table in Firestore
            val tagType = selectedTag.tagType
            val isSelected = selectedTags.contains(tagId)
            profileTagsRepository.associateTagWithUser(userId, tagId, tagType, isSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return interestTags.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = interestTags[position]
        holder.bind(tag)
        holder.itemView.setOnClickListener {
            holder.toggleSelection()
            onTagClickListener.invoke(tag)
        }
    }

    fun updateData(availableInterestTags: List<ProfileTags>) {
        interestTags = availableInterestTags
        notifyDataSetChanged()


    }

    }

