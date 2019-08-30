package hse24.shop.details.mvi

import hse24.common.mvi.MviInteractor
import hse24.di.IoScheduler
import hse24.shop.details.di.ProductDetailsScope
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ProductDetailsScope
class ProductDetailsInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler
) : MviInteractor<ProductDetailsAction, ProductDetailsResult> {

    private val initProcessor: ObservableTransformer<ProductDetailsAction.Init, ProductDetailsResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    Observable.fromCallable {

                    }
                        .map {
                            ProductDetailsResult.Error as ProductDetailsResult
                        }
                        .subscribeOn(ioScheduler)
                        .startWith(ProductDetailsResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            ProductDetailsResult.Error
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
                                .compose(initProcessor)
                        )
                    )
                }
        }
}
