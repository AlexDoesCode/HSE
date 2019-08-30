package hse24.shop.catalog.mvi

import hse24.common.mvi.MviInteractor
import hse24.di.IoScheduler
import hse24.shop.catalog.di.CatalogScope
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@CatalogScope
class CatalogInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler
) : MviInteractor<CatalogAction, CatalogResult> {

    private val initProcessor: ObservableTransformer<CatalogAction.Init, CatalogResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    Observable.fromCallable {

                    }
                        .map {
                            CatalogResult.Error as CatalogResult
                        }
                        .subscribeOn(ioScheduler)
                        .startWith(CatalogResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CatalogResult.Error
                        }
                }

        }

    override fun actionProcessor(): ObservableTransformer<in CatalogAction, out CatalogResult> =
        ObservableTransformer { action ->
            action
                .observeOn(ioScheduler)
                .publish { shared ->
                    Observable.merge(
                        listOf(
                            shared.ofType(CatalogAction.Init::class.java)
                                .compose(initProcessor)
                        )
                    )

                }
        }
}
