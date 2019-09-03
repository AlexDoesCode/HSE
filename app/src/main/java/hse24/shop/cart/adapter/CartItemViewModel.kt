package hse24.shop.cart.adapter

sealed class CartItemViewModel(open val id: Int) {

    companion object {
        const val EMPTY_ITEM_ID = 0
        const val SUM_ITEM_ID = Integer.MAX_VALUE
    }

    data class ProductItem(
        override val id: Int,
        val sku: Int,
        val brandName: String,
        val name: String,
        val variation: String,
        val imageUrl: String,
        val price: Float,
        val currency: String
    ) : CartItemViewModel(id)

    data class OverallSum(
        val sum: Float,
        val currency: String
    ) : CartItemViewModel(SUM_ITEM_ID)

    object EmptyCart : CartItemViewModel(EMPTY_ITEM_ID)
}
