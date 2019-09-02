package hse24.shop.repository

data class CategoryModel(
    val id: Int,
    val name: String,
    val parentId: Int
)
