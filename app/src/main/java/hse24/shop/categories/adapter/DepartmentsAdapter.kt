package hse24.shop.categories.adapter

import hse24.common.android.adapter.DelegatedAdapter
import hse24.common.android.adapter.TypedAdapterDelegate
import hse24.common.android.adapter.ViewHolderRenderer
import io.reactivex.Completable

class DepartmentsAdapter(
    private val categoriesClickListener: CategoriesAdapter.CategoryItemCallbackClickListener
) : DelegatedAdapter() {

    private companion object {
        const val DEPARTMENT_ID = 0
    }

    init {
        addDelegate(DEPARTMENT_ID, TypedAdapterDelegate { parent ->
            ViewHolderRenderer(DepartmentLayout(parent.context, categoriesClickListener))
        })
    }

    override fun getItemViewType(position: Int): Int {
        return DEPARTMENT_ID
    }

    override var items: List<DepartmentViewModel> = emptyList()

    override fun getItemCount() = items.size

    fun setItems(newItems: List<DepartmentViewModel>): Completable =
        Completable.fromAction {
            items = newItems
            notifyDataSetChanged()
        }
}
