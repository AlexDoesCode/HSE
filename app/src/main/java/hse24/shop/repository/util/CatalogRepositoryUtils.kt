package hse24.shop.repository.util

import hse24.db.entity.CategoryEntity
import hse24.db.entity.toCategoryEntity
import hse24.network.model.CategoryApiModel

internal class CategoriesTree(
    root: CategoryApiModel
) {

    val items = mutableListOf<CategoryEntity>()

    init {
        root.children?.forEach {
            readItems(it, null)
        }
    }

    private fun readItems(root: CategoryApiModel, parentId: Int?) {
        items.add(root.toCategoryEntity(parentId))
        if (root.children == null || root.children.isEmpty()) {
            return
        } else {
            root.children.forEach { readItems(it, root.categoryId) }
        }
    }
}
