package hse24.shop.details.mvi

import hse24.common.mvi.MviInteractor
import hse24.db.entity.toProductDetailsViewModel
import hse24.di.IoScheduler
import hse24.shop.details.di.ProductDetailsScope
import hse24.shop.usecase.details.AddProductToCartUseCase
import hse24.shop.usecase.details.FetchProductBySkuUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ProductDetailsScope
class ProductDetailsInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val fetchProductDetailsBySkuUseCase: FetchProductBySkuUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase
) : MviInteractor<ProductDetailsAction, ProductDetailsResult> {

    private val initProcessor: ObservableTransformer<ProductDetailsAction.Init, ProductDetailsResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    fetchProductDetailsBySkuUseCase.execute(it.sku)
                        .toObservable()
                        .map { model ->
                            ProductDetailsResult.ProductData(model.toProductDetailsViewModel()) as ProductDetailsResult
                        }
                        .subscribeOn(ioScheduler)
                        .startWith(ProductDetailsResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            ProductDetailsResult.NetworkError
                        }
                }
        }

    private val fetchProductProcessor: ObservableTransformer<ProductDetailsAction.LoadProductVariation, ProductDetailsResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    fetchProductDetailsBySkuUseCase.execute(it.sku)
                        .toObservable()
                        .map { model ->
                            ProductDetailsResult.ProductData(model.toProductDetailsViewModel()) as ProductDetailsResult
                        }
                        .subscribeOn(ioScheduler)
                        .startWith(ProductDetailsResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            ProductDetailsResult.NetworkError
                        }
                }
        }

    private val addToCartProcessorProcessor: ObservableTransformer<ProductDetailsAction.AddProductToCart, ProductDetailsResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    addProductToCartUseCase.execute()
                        .map {
                            ProductDetailsResult.CartAdditionResult(it) as ProductDetailsResult
                        }
                        .subscribeOn(ioScheduler)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            ProductDetailsResult.DataError
                        }
                }
        }

    override fun actionProcessor(): ObservableTransformer<in ProductDetailsAction, out ProductDetailsResult> =
        ObservableTransformer { action ->
            action
                .observeOn(ioScheduler)
                .publish { shared ->
                    Observable.merge(
                        listOf(
                            shared.ofType(ProductDetailsAction.Init::class.java)
                                .compose(initProcessor),
                            shared.ofType(ProductDetailsAction.LoadProductVariation::class.java)
                                .compose(fetchProductProcessor),
                            shared.ofType(ProductDetailsAction.AddProductToCart::class.java)
                                .compose(addToCartProcessorProcessor)
                        )
                    )
                }
        }
}
