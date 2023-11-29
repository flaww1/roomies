package pt.ipca.roomies.ui.authentication.registration.registrationsteps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.databinding.ItemInterestBinding
import pt.ipca.roomies.data.entities.TagType
import ProfileTagsRepository

class InterestTagsAdapter(
    private var interestTags: List<ProfileTags>,
    private val onTagClickListener: (ProfileTags) -> Unit,
    private val profileTagsRepository: ProfileTagsRepository,
    var userId: String,
    private val selectedTagsByType: MutableMap<TagType, MutableSet<ProfileTags>>
) : RecyclerView.Adapter<InterestTagsAdapter.ViewHolder>() {
    fun toggleSelection(tag: ProfileTags) {
        val selectedTagType = tag.tagType
        val selectedTagsForType = selectedTagsByType.getOrPut(selectedTagType) { mutableSetOf() }

        if (selectedTagsForType.contains(tag)) {
            selectedTagsForType.remove(tag)
        } else if (selectedTagsForType.size < 5) {
            selectedTagsForType.add(tag)
        }

        notifyItemChanged(interestTags.indexOf(tag))

        val tagId = tag.tagId
        val tagType = tag.tagType
        val isSelected = selectedTagsForType.contains(tag)

        profileTagsRepository.associateTagWithUser(userId, tagId, tagType, isSelected)
    }
    inner class ViewHolder(private val binding: ItemInterestBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = interestTags[position]
                    toggleSelection(tag)
                    onTagClickListener.invoke(tag)
                }
            }
        }

        fun bind(tag: ProfileTags) {
            binding.textInterestTag.text = tag.tagName
            binding.checkboxInterest.isChecked = selectedTagsByType[tag.tagType]?.contains(tag) == true
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInterestBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = interestTags[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return interestTags.size
    }

    override fun getItemViewType(position: Int): Int {
        val tag = interestTags[position]
        return tag.tagType.ordinal
    }

    fun updateData(availableInterestTags: List<ProfileTags>) {
        interestTags = availableInterestTags
        notifyItemChanged(0, interestTags.size)
    }

    fun updateUserId(userId: String) {
        this.userId = userId
        notifyItemChanged(0, interestTags.size)
    }



}
