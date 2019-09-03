package hse24.db.entity

import hse24.common.util.ImageSize
import hse24.common.util.ImageUtils
import hse24.network.model.CategoryApiModel
import hse24.network.model.ProductApiModel
import hse24.network.model.ProductDetailsApiModel
import hse24.network.model.ProductVariationsApiModel
import hse24.shop.cart.adapter.CartItemViewModel
import hse24.shop.catalog.adapter.CatalogItemViewModel
import hse24.shop.categories.adapter.CategoryItemViewModel
import hse24.shop.categories.adapter.DepartmentViewModel
import hse24.shop.details.model.ProductDetailsViewModel
import hse24.shop.details.model.ProductVariationViewModel
import hse24.shop.repository.CategoryModel
import hse24.shop.repository.ProductDetailsModel
import hse24.shop.repository.ProductVariationModel

fun CategoryEntity.toCategoryModel() = CategoryModel(
    id = this.id,
    name = this.name,
    parentId = this.parentId
)

fun CategoryModel.toCategoryViewModel(hasChildren: Boolean) =
    CategoryItemViewModel.CategoryViewModel(
        id = this.id,
        name = this.name,
        parentId = this.parentId,
        hasSubcategories = hasChildren
    )

fun CategoryModel.toSubcategoryViewModel() =
    CategoryItemViewModel.SubcategoryViewModel(
        id = this.id,
        name = this.name,
        parentId = this.parentId
    )

fun CategoryModel.toDepartmentViewModel() = DepartmentViewModel(
    id = this.id,
    name = this.name
)

fun CategoryApiModel.toCategoryEntity(parentId: Int? = null) = CategoryEntity(
    id = this.categoryId!!,
    name = this.displayName!!,
    parentId = when (parentId) {
        null -> CategoryEntity.DEPARTMENT_PARENT_ID
        else -> parentId
    }
)

fun ProductApiModel.toCatalogEntity(categoryId: Int) = CatalogEntity(
    id = CatalogEntity.INSERT_ID,
    sku = this.sku,
    brandName = this.brandNameLong ?: "UNKNOWN",
    productName = this.nameShort ?: "UNKNOWN",
    price = this.productPrice.price ?: 0f,
    currencySymbol = this.productPrice.currency ?: "",
    imageUrl = ImageUtils.getImageUrl(this.imageUris?.first() ?: "", ImageSize.SMALL),
    categoryIdFk = categoryId
)

fun CatalogEntity.toProductViewModel() = CatalogItemViewModel.ProductViewModel(
    id = this.id,
    sku = this.sku,
    brandName = this.brandName,
    name = this.productName,
    price = this.price,
    currency = this.currencySymbol,
    imageUrl = this.imageUrl
)

fun ProductDetailsModel.toCartEntity() = CartEntity(
    id = CartEntity.CART_INSERT_ID,
    sku = this.sku,
    brand = this.brandName,
    name = this.name,
    variation = this.variations.firstOrNull { it.sku == this.sku }?.name ?: "UNKNOWN",
    imageUrl = this.imageUris.firstOrNull() ?: "",
    price = this.price,
    currency = this.currency
)

fun ProductDetailsApiModel.toProductModel() = ProductDetailsModel(
    sku = this.sku,
    imageUris = when (this.variations.isNullOrEmpty()) {
        true -> this.imageUris ?: emptyList()
        false -> variations.firstOrNull { it.sku == this.sku }?.imageUris ?: this.imageUris ?: emptyList()
    },
    title = this.title ?: "UNKNOWN",
    name = this.nameShort ?: "UNKNOWN",
    brandName = this.brandNameShort ?: this.brandNameLong ?: "UNKNOWN",
    description = this.longDescription ?: "",
    variationName = this.variations.firstOrNull { it.sku == this.sku }?.variationType ?: "UNKNOWN ",
    price = this.productPrice.price ?: 0f,
    currency = this.productPrice.currency ?: "",
    variations = this.variations.map { it.toProductVariationModel() }
)

fun ProductVariationsApiModel.toProductVariationModel() = ProductVariationModel(
    sku = this.sku,
    name = this.variationType ?: "UNKNOWN"
)

fun ProductDetailsModel.toProductDetailsViewModel() = ProductDetailsViewModel(
    sku = this.sku,
    imageUrls = this.imageUris.map { ImageUtils.getImageUrl(it, ImageSize.LARGE) },
    title = this.title,
    name = this.name,
    description = this.description,
    price = this.price,
    currency = this.currency,
    brandName = this.brandName,
    currentVariation = this.variationName,
    variations = this.variations.map { ProductVariationViewModel(it.sku, it.name) }
)

fun CartEntity.toCartProductViewModel() = CartItemViewModel.ProductItem(
    id = this.id,
    sku = this.sku,
    brandName = this.brand,
    name = this.name,
    variation = this.variation,
    imageUrl = ImageUtils.getImageUrl(this.imageUrl, ImageSize.SMALL),
    price = this.price,
    currency = this.currency
)
