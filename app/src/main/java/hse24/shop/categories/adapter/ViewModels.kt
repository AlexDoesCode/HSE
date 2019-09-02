package hse24.shop.categories.adapter

sealed class CategoryItemViewModel(
    open val id: Int,
    open val parentId: Int
) {

    data class CategoryViewModel(
        override val id: Int,
        val name: String,
        val hasSubcategories: Boolean,
        var isExpanded: Boolean = false,
        override val parentId: Int
    ) : CategoryItemViewModel(id, parentId)

    data class SubcategoryViewModel(
        override val id: Int,
        val name: String,
        override val parentId: Int
    ) : CategoryItemViewModel(id, parentId)

}

data class DepartmentViewModel(
    val id: Int,
    val name: String
)
