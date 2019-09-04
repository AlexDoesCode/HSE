package hse24.shop.details.mvi

import hse24.common.mvi.*
import hse24.shop.details.model.ProductDetailsViewModel

sealed class ProductDetailsIntention : MviIntention {

    object GetLastState : ProductDetailsIntention()
    data class Init(val sku: Int) : ProductDetailsIntention(), MviInitIntention
    data class LoadProductVariation(val sku: Int) : ProductDetailsIntention()

    object AddProductToCart : ProductDetailsIntention()
}

sealed class ProductDetailsAction : MviAction {

    object GetLastState : ProductDetailsAction()
    data class Init(val sku: Int) : ProductDetailsAction()
    data class LoadProductVariation(val sku: Int) : ProductDetailsAction()

    object AddProductToCart : ProductDetailsAction()

}

sealed class ProductDetailsResult : MviResult {

    object LastState : ProductDetailsResult()
    object Loading : ProductDetailsResult()
    object DataError : ProductDetailsResult()
    object NetworkError : ProductDetailsResult()

    data class ProductData(val viewModel: ProductDetailsViewModel) : ProductDetailsResult()
    data class CartAdditionResult(val wasAdded: Boolean) : ProductDetailsResult()

}

data class ProductDetailsState(
    val isLoading: Boolean = false,
    val addToCartState: OneShot<Boolean> = OneShot.empty(),
    val error: OneShot<ProductDetailsError> = OneShot.empty(),
    val productData: ProductDetailsViewModel? = null
) : ViewStateWithId(), MviState

enum class ProductDetailsError {
    NETWORK, DATA
}
