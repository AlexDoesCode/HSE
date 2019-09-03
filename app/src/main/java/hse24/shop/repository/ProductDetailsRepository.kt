package hse24.shop.repository

import hse24.db.dao.CartDao
import hse24.db.entity.toCartEntity
import hse24.db.entity.toProductModel
import hse24.di.IoScheduler
import hse24.network.ShoppingApi
import hse24.shop.details.di.ProductDetailsScope
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@ProductDetailsScope
class ProductDetailsRepository @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val shoppingApi: ShoppingApi,
    private val cartDao: CartDao
) {

    private val productPublisher = BehaviorSubject.create<ProductDetailsModel>()

    fun fetchProductDetails(productSku: Int) =
        shoppingApi.fetchProductDetailsBySku(productSku)
            .map {
                it.toProductModel()
            }
            .doOnSuccess {
                productPublisher.onNext(it)
            }

    //Returns TRUE if added
    //Returns FALSE if already added
    fun addProductToCart(): Observable<Boolean> =
        productPublisher
            .switchMap { productDetailsModel ->
                cartDao.getProductsFromCartBySku(productDetailsModel.sku)
                    .observeOn(ioScheduler)
                    .toObservable()
                    .map { entities ->
                        val isPresent = entities.firstOrNull { item -> item.sku == productDetailsModel.sku } != null
                        if (isPresent.not()) {
                            cartDao.insert(productDetailsModel.toCartEntity())
                        }
                        isPresent.not()
                    }
            }
}
