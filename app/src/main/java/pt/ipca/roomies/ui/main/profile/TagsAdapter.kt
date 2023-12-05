package pt.ipca.roomies.ui.main.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.repositories.ProfileTagsRepository
import pt.ipca.roomies.databinding.ItemInterestBinding

class TagsAdapter(
    private var tags: List<ProfileTags>,
    private val onTagClickListener: (ProfileTags, Boolean) -> Unit,
    private val profileTagsRepository: ProfileTagsRepository,
    private var userId: String,
    private val selectedTagsByType: MutableMap<TagType, MutableLiveData<MutableSet<ProfileTags>>> = mutableMapOf(),
    private val selectedTags: MutableSet<ProfileTags>
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    fun isTagSelected(tag: ProfileTags): Boolean {
        return selectedTags.contains(tag)
    }

    fun toggleSelection(holder: ViewHolder, tag: ProfileTags) {
        val selectedTagType = tag.tagType
        val selectedTagsForType = selectedTagsByType[selectedTagType]?.value ?: mutableSetOf()

        if (selectedTagsForType.contains(tag)) {
            selectedTagsForType.remove(tag)
            Log.d("TagsAdapter", "Tag removed: $tag")
        } else if (selectedTagsForType.size < 5) {
            selectedTagsForType.add(tag)
            Log.d("TagsAdapter", "Tag added: $tag")
        }

        // Update the MutableLiveData
        selectedTagsByType[selectedTagType]?.value = selectedTagsForType

        // Update the CheckBox state based on the isTagSelected function
        holder.binding.checkboxInterest.isChecked = isTagSelected(tag)

        // Notify the adapter that the item's visual state has changed
    }

    inner class ViewHolder(val binding: ItemInterestBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = tags[position]
                    onTagClickListener.invoke(tag, !isTagSelected(tag))
                }
            }

            binding.checkboxInterest.setOnCheckedChangeListener { _, isChecked ->
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = tags[position]
                    toggleSelection(tag, isChecked)
                }
            }
        }

        fun bind(tag: ProfileTags) {
            binding.textInterestTag.text = tag.tagName
            // Set the initial state of the checkbox
            binding.checkboxInterest.isChecked = isTagSelected(tag)
        }

        private fun toggleSelection(tag: ProfileTags, isChecked: Boolean) {
            val selectedTagType = tag.tagType
            val selectedTagsForType = selectedTagsByType[selectedTagType]?.value ?: mutableSetOf()

            if (isChecked && !selectedTagsForType.contains(tag) && selectedTagsForType.size < 5) {
                selectedTagsForType.add(tag)
                Log.d("TagsAdapter", "Tag added: $tag")
            } else if (!isChecked && selectedTagsForType.contains(tag)) {
                selectedTagsForType.remove(tag)
                Log.d("TagsAdapter", "Tag removed: $tag")
            }

            // Update the MutableLiveData
            selectedTagsByType[selectedTagType]?.value = selectedTagsForType
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInterestBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun updateData(newTags: List<ProfileTags>, tagType: TagType) {
        tags = newTags.filter { it.tagType == tagType }
        notifyItemChanged(0, tags.size)
    }

    fun updateUserId(newUserId: String) {
        userId = newUserId
        notifyItemChanged(0, tags.size)
    }
}
