package hse24.network.model

data class CategoryApiModel(
    val categoryId: Int?,
    val displayName: String?,
    val resultCount: Int?,
    val children: List<CategoryApiModel>?
)

data class CatalogPageApiModel(
    val productResults: List<ProductApiModel>,
    val paging: PageDataApiModel,
    val resultCount: Int
)

data class ProductApiModel(
    val sku: Int,
    val nameShort: String?,
    val brandNameLong: String?,
    val imageUris: List<String>?,
    val productPrice: PriceApiModel
)

data class PriceApiModel(
    val price: Float?,
    val currency: String?,
    val shippingCosts: Float?,
    val priceDiscount: Float?
)

data class PageDataApiModel(
    val pageSize: Int,
    val page: Int,
    val numPages: Int
)
