package hse24.shop.usecase.cart

import hse24.shop.cart.di.CartScope
import hse24.shop.repository.CartRepository
import io.reactivex.Completable
import javax.inject.Inject

@CartScope
class RemoveCartItemUseCase @Inject constructor(
    private val repository: CartRepository
) {

    fun execute(itemSku: Int): Completable =
        repository.removeItemFromCart(itemSku)
}
