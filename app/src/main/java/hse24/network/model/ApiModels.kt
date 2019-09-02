package hse24.network.model

data class CategoryApiModel(
    val categoryId: Int?,
    val displayName: String?,
    val resultCount: Int?,
    val children: List<CategoryApiModel>?
)

data class CategoryItemModel(
    val categoryId: Int
)

data class ProductModel(
    val id: Int
)
