package hse24.shop.cart.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.cart.adapter.CartItemViewModel
import hse24.shop.cart.di.CartScope
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@CartScope
class CartPresenter @Inject constructor(
    interactor: CartInteractor
) : MviBasePresenter<CartIntention, CartAction, CartResult, CartState>(interactor) {

    override val defaultState: CartState
        get() = CartState()

    override fun actionFromIntention(intent: CartIntention): CartAction =
        when (intent) {
            is CartIntention.Init -> CartAction.Init
            is CartIntention.RemoveItem -> CartAction.RemoveAction(intent.sku)
        }

    override val reducer: BiFunction<CartState, CartResult, CartState>
        get() = BiFunction { prevState, result ->
            when (result) {
                is CartResult.CartItems -> {
                    prevState.copy(
                        cartItems = mutableListOf<CartItemViewModel>()
                            .apply {
                                when (result.cartItems.size) {
                                    0 -> add(CartItemViewModel.EmptyCart)
                                    else -> {
                                        addAll(result.cartItems)

                                        add(
                                            CartItemViewModel.OverallSum(
                                                result.cartItems.map { (it as CartItemViewModel.ProductItem).price }.sum(),
                                                (result.cartItems.first() as CartItemViewModel.ProductItem).currency
                                            )
                                        )
                                    }
                                }
                            }
                    )
                }
                CartResult.Error -> prevState.copy(
                    error = OneShot(true)
                )
            }
        }
}
