package hse24.shop.cart.mvi

import hse24.common.mvi.*
import hse24.shop.cart.adapter.CartItemViewModel

sealed class CartIntention : MviIntention {

    object Init : CartIntention(), MviInitIntention
    data class RemoveItem(val sku: Int) : CartIntention()
}

sealed class CartAction : MviAction {

    object Init : CartAction()
    data class RemoveAction(val sku: Int) : CartAction()
}

sealed class CartResult : MviResult {

    object Error : CartResult()

    data class CartItems(val cartItems: List<CartItemViewModel>) : CartResult()
}

data class CartState(
    val error: OneShot<Boolean> = OneShot.empty(),
    val cartItems: List<CartItemViewModel>? = null
) : MviState, ViewStateWithId()
