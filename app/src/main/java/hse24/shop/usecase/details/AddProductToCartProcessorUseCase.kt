package hse24.shop.usecase.details

import hse24.shop.details.di.ProductDetailsScope
import hse24.shop.repository.ProductDetailsRepository
import io.reactivex.Observable
import javax.inject.Inject

@ProductDetailsScope
class AddProductToCartProcessorUseCase @Inject constructor(
    private val repository: ProductDetailsRepository
) {

    fun execute(): Observable<Boolean> =
        repository.addProductToCart()
}
