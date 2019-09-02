package hse24.shop.cart.mvi

import hse24.common.mvi.*
import hse24.shop.cart.adapter.CartItemViewModel

sealed class CartIntention : MviIntention {

    object Init : CartIntention(), MviInitIntention

    data class RemoveItem(val id: Int) : CartIntention()

    //TODO: Add other intentions?
}

sealed class CartAction : MviAction {

    object Init : CartAction()

    data class RemoveAction(val id: Int) : CartAction()

    //TODO: Add actions aswell?
}

sealed class CartResult : MviResult {

    object Loading : CartResult()
    object Error : CartResult()

    data class CartItems(val cartItems: List<CartItemViewModel>) : CartResult()
}

data class CartState(
    val isLoading: Boolean = false,
    //TODO: Consider an error type if needed
    val error: OneShot<Boolean> = OneShot.empty(),
    val cartItems: List<CartItemViewModel>? = null
) : MviState, ViewStateWithId()
