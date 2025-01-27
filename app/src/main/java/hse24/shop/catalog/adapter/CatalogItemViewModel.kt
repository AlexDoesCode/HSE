package hse24.shop.catalog.adapter

sealed class CatalogItemViewModel(open val id: Int) {

    private companion object {
        const val LOAD_MORE_ID = Integer.MAX_VALUE
        const val EMPTY_ID = 0
    }

    object LoadMore : CatalogItemViewModel(LOAD_MORE_ID)
    object Empty : CatalogItemViewModel(0)

    data class ProductViewModel(
        override val id: Int,
        val sku: Int,
        val brandName: String,
        val name: String,
        val price: Float,
        val currency: String,
        val imageUrl: String
    ) : CatalogItemViewModel(id)

}
