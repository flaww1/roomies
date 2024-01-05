package pt.ipca.roomies.ui.main.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags
import pt.ipca.roomies.data.repositories.ProfileTagRepository
import pt.ipca.roomies.databinding.ItemInterestBinding

class TagsAdapter(
    private var tags: List<UserTags>,
    private val onTagClickListener: (UserTags, Boolean) -> Unit,
    private val recyclerView: RecyclerView,
    private val profileTagsRepository: ProfileTagRepository,
    private var userId: String,
    private val selectedTagsByType: MutableMap<TagType, MutableLiveData<MutableSet<UserTags>>> = mutableMapOf(),
    private val selectedTags: MutableSet<UserTags>
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    fun isTagSelected(tag: UserTags): Boolean {
        return selectedTags.contains(tag)
    }

    fun toggleSelection(holder: ViewHolder, tag: UserTags) {
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
        notifyItemChanged(holder.absoluteAdapterPosition)
    }

    inner class ViewHolder(val binding: ItemInterestBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = tags[position]
                    toggleSelection(this, tag) // Call toggleSelection function
                }
            }

            binding.checkboxInterest.setOnCheckedChangeListener { _, _ ->
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = tags[position]
                    toggleSelection(this, tag) // Call toggleSelection function
                }
            }
        }

        fun bind(tag: UserTags) {
            binding.textInterestTag.text = tag.tagName
            // Set the initial state of the checkbox using isTagSelected method
            binding.checkboxInterest.isChecked = isTagSelected(tag)
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

    fun updateData(newTags: List<UserTags>, tagType: TagType) {
        recyclerView.post {
            tags = newTags.filter { it.tagType == tagType }
            notifyItemChanged(0, tags.size)
        }
    }

    // Improve method name for consistency
    fun updateUserIdAndNotifyDataSetChanged(newUserId: String) {
        recyclerView.post {
            userId = newUserId
            notifyItemChanged(0, tags.size)
        }
    }

    fun updateUserId(s: String) {
        userId = s

    }
}
