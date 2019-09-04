package hse24.network.model

data class CategoryApiModel(
    val categoryId: Int?,
    val displayName: String?,
    val resultCount: Int?,
    val children: List<CategoryApiModel>?
)

data class CatalogPageApiModel(
    val productResults: List<ProductApiModel>,
    val paging: PageDataApiModel?,
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

data class ProductDetailsApiModel(
    val sku: Int,
    val imageUris: List<String>?,
    val title: String?,
    val nameShort: String?,
    val longDescription: String?,
    val productPrice: PriceApiModel,
    val variations: List<ProductVariationsApiModel>,
    val brandNameShort: String?,
    val brandNameLong: String?
)

data class ProductVariationsApiModel(
    val sku: Int,
    val imageUris: List<String>,
    val variationType: String?
)
