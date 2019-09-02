package hse24.common.android.adapter

import androidx.recyclerview.widget.DiffUtil
import hse24.shop.categories.adapter.CategoryItemViewModel
import hse24.shop.categories.adapter.DepartmentViewModel

class CommonDiffUtilCallback(
    private val oldItems: List<Any>,
    private val newItems: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        val sameClass = oldItem::class == newItem::class
        return if (!sameClass) false
        else {
            when (oldItem) {
                is CategoryItemViewModel -> oldItem.id == (newItem as CategoryItemViewModel).id
                is DepartmentViewModel -> oldItem.id == (newItem as DepartmentViewModel).id
                else -> areContentsTheSame(oldItemPosition, newItemPosition)
            }
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}
