package hse24.shop.usecase.details

import hse24.shop.details.di.ProductDetailsScope
import hse24.shop.repository.ProductDetailsRepository
import javax.inject.Inject

@ProductDetailsScope
class FetchProductBySkuUseCase @Inject constructor(
    private val repository: ProductDetailsRepository
) {

    fun execute(sku: Int) =
        repository.fetchProductDetails(sku)
}
