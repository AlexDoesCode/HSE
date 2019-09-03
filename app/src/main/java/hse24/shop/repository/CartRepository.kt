package hse24.shop.repository

import hse24.db.dao.CartDao
import hse24.db.entity.CartEntity
import hse24.di.IoScheduler
import hse24.shop.cart.di.CartScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject

@CartScope
class CartRepository @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val cartDao: CartDao
) {

    fun observeCart(): Observable<List<CartEntity>> =
        cartDao.observeCart()
            .toObservable()
            .observeOn(ioScheduler)

    fun removeItemFromCart(itemSku: Int) =
        Completable.fromAction {
            cartDao.removeEntity(itemSku)
        }

}
