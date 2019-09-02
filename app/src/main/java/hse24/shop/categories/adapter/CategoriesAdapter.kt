package hse24.shop.categories.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import hse24.common.android.adapter.CommonDiffUtilCallback
import hse24.common.android.adapter.DelegatedAdapter
import hse24.common.android.adapter.TypedAdapterDelegate
import hse24.common.android.adapter.ViewHolderRenderer
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CategoriesAdapter(
    private val categoryClickListener: CategoryItemCallbackClickListener
) : DelegatedAdapter() {

    private companion object {
        const val TYPE_ITEM_CATEGORY = 0
        const val TYPE_ITEM_SUBCATEGORY = 1
    }

    init {
        addDelegate(TYPE_ITEM_CATEGORY, TypedAdapterDelegate { parent ->
            val layout = CategoryLayout(parent.context, categoryClickListener)
            ViewHolderRenderer(layout)
        })
        addDelegate(TYPE_ITEM_SUBCATEGORY, TypedAdapterDelegate { parent ->
            val layout = SubcategoryLayout(parent.context, categoryClickListener)
            ViewHolderRenderer(layout)
        })
    }

    override var items: List<CategoryItemViewModel> = emptyList()

    override fun getItemViewType(position: Int) = when (items[position]) {
        is CategoryItemViewModel.CategoryViewModel -> TYPE_ITEM_CATEGORY
        is CategoryItemViewModel.SubcategoryViewModel -> TYPE_ITEM_SUBCATEGORY
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<CategoryItemViewModel>): Completable {
        val categoriesListItems = buildCategoriesList(newItems)
        return Single.fromCallable<DiffUtil.DiffResult> {
            DiffUtil.calculateDiff(CommonDiffUtilCallback(items, categoriesListItems))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { result ->
                this.items = categoriesListItems
                result.dispatchUpdatesTo(this)
            }
            .ignoreElement()
    }

    private fun buildCategoriesList(allCategories: List<CategoryItemViewModel>): List<CategoryItemViewModel> {

        val categories = allCategories.filterIsInstance<CategoryItemViewModel.CategoryViewModel>()
        val subcategories = allCategories.filterIsInstance<CategoryItemViewModel.SubcategoryViewModel>()

        return if (subcategories.isEmpty()) {
            categories.map {
                it.apply {
                    if (hasSubcategories) {
                        isExpanded = false
                    }
                }
            }
        } else {
            mutableListOf<CategoryItemViewModel>()
                .apply {
                    val expandedCategoryId = subcategories.first().parentId
                    val indexOfExpandedCategory = categories.indexOfFirst { it.id == expandedCategoryId }
                    categories.forEach {
                        it.isExpanded = it.hasSubcategories && it.id == expandedCategoryId
                    }
                    addAll(categories.subList(0, indexOfExpandedCategory + 1))
                    addAll(subcategories)
                    addAll(
                        categories.subList(
                            indexOfExpandedCategory + 1,
                            categories.size
                        )
                    )
                }
        }
    }

    interface CategoryItemCallbackClickListener {

        fun onDepartmentClick(id: Int)
        fun onCategoryRendered(id: Int, view: View)
        fun onCategoryClick(id: Int, isExpanded: Boolean, hasSubcategories: Boolean)
        fun onSubcategoryClick(id: Int)
    }
}
