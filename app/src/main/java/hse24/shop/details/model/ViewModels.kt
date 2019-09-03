package hse24.shop.details.model

data class ProductDetailsViewModel(
    val sku: Int,
    val imageUrls: List<String>,
    val title: String,
    val name: String,
    val description: String,
    val price: Float,
    val currency: String,
    val brandName: String,
    val currentVariation: String,
    val variations: List<ProductVariationViewModel>
)

data class ProductVariationViewModel(
    val sku: Int,
    val name: String
)

//data class ProductVariationViewModel(
//    val sku: Int,
//    val name: String,
//    val imageUrl: String,
//    val price: String,
//    val brandName: String
//)
