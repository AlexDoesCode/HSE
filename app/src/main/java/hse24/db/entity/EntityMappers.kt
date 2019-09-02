package hse24.db.entity

import hse24.common.util.ImageSize
import hse24.common.util.ImageUtils
import hse24.network.model.CategoryApiModel
import hse24.network.model.ProductApiModel
import hse24.shop.catalog.adapter.CatalogItemViewModel
import hse24.shop.categories.adapter.CategoryItemViewModel
import hse24.shop.categories.adapter.DepartmentViewModel
import hse24.shop.repository.CategoryModel

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
    price = "${this.price} ${this.currencySymbol}",
    imageUrl = this.imageUrl
)
