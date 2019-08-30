package hse24.network.model

data class CategoriesModel(
    val categoryId: Int?,
    val displayName: String?,
    val resultCount: Int?,
    val parentCategoryId: Int?,
    val children: List<CategoriesModel>?
)

data class CategoryItemModel(
    val categoryId: Int
)

data class CategoryModel(
    val id: Int
)

data class ProductModel(
    val id: Int
)
