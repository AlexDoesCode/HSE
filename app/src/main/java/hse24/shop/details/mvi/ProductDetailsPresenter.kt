package hse24.shop.details.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.details.di.ProductDetailsScope
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@ProductDetailsScope
class ProductDetailsPresenter @Inject constructor(
    interactor: ProductDetailsInteractor
) : MviBasePresenter<ProductDetailsIntention, ProductDetailsAction, ProductDetailsResult, ProductDetailsState>(
    interactor
) {

    override val defaultState: ProductDetailsState
        get() = ProductDetailsState()

    override fun actionFromIntention(intent: ProductDetailsIntention): ProductDetailsAction =
        when (intent) {
            is ProductDetailsIntention.Init -> ProductDetailsAction.Init(intent.sku)
            ProductDetailsIntention.AddProductToCart -> ProductDetailsAction.AddProductToCart
            is ProductDetailsIntention.LoadProductVariation -> ProductDetailsAction.LoadProductVariation(intent.sku)
        }

    override val reducer: BiFunction<ProductDetailsState, ProductDetailsResult, ProductDetailsState>
        get() = BiFunction { prevState, result ->
            when (result) {
                ProductDetailsResult.Loading -> prevState.copy(
                    isLoading = true
                )
                is ProductDetailsResult.ProductData -> prevState.copy(
                    isLoading = false,
                    productData = result.viewModel
                )
                is ProductDetailsResult.CartAdditionResult -> prevState.copy(
                    isLoading = false,
                    addToCartState = OneShot(result.wasAdded)
                )
                ProductDetailsResult.DataError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(ProductDetailsError.DATA)
                )
                ProductDetailsResult.NetworkError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(ProductDetailsError.NETWORK)
                )
            }
        }
}
