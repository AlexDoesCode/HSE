package hse24.shop.details.mvi

import hse24.common.mvi.*
import hse24.shop.details.ProductDetailsViewModel

sealed class ProductDetailsIntention() : MviIntention {

    object Init : ProductDetailsIntention(), MviInitIntention

}

sealed class ProductDetailsAction : MviAction {

    object Init : ProductDetailsAction()

}

sealed class ProductDetailsResult : MviResult {

    object Loading : ProductDetailsResult()
    object Error : ProductDetailsResult()

    //TODO: Consider passing raw data to presenter for converting to view model
    data class ProductData(val viewModel: ProductDetailsViewModel) : ProductDetailsResult()

}

data class ProductDetailsState(
    val isLoading: Boolean = false,
    val error: OneShot<Boolean> = OneShot.empty(),
    val productData: ProductDetailsViewModel? = null
) : ViewStateWithId(), MviState
