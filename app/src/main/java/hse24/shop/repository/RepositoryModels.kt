package hse24.shop.repository

data class CategoryModel(
    val id: Int,
    val name: String,
    val parentId: Int
)

data class ProductDetailsModel(
    val sku: Int,
    val imageUris: List<String>,
    val title: String,
    val name: String,
    val brandName: String,
    val description: String,
    val variationName: String,
    val price: Float,
    val currency: String,
    val variations: List<ProductVariationModel>
)

data class ProductVariationModel(
    val sku: Int,
    val name: String
)
