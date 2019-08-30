package hse24.shop.details.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.details.ProductDetailsViewModel
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
            ProductDetailsIntention.Init -> ProductDetailsAction.Init
        }

    override val reducer: BiFunction<ProductDetailsState, ProductDetailsResult, ProductDetailsState>
        get() = BiFunction { prevState, result ->
            when (result) {
                ProductDetailsResult.Loading -> prevState.copy(
                    isLoading = true
                )
                ProductDetailsResult.Error -> prevState.copy(
                    isLoading = false,
                    error = OneShot(true)
                )
                is ProductDetailsResult.ProductData -> prevState.copy(
                    isLoading = false,
                    //TODO: Consider applying data mapping
                    productData = ProductDetailsViewModel(
                        id = result.viewModel.id
                    )
                )
            }
        }

}
