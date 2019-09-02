package hse24.shop.catalog.adapter

sealed class CatalogItemViewModel(open val id: Int) {

    private companion object {
        const val LOAD_MORE_ID = Integer.MAX_VALUE
    }

    object LoadMore : CatalogItemViewModel(LOAD_MORE_ID)

    data class ProductViewModel(
        override val id: Int,
        val sku: Int,
        val brandName: String,
        val name: String,
        val price: String,
        val imageUrl: String
    ) : CatalogItemViewModel(id)

}
