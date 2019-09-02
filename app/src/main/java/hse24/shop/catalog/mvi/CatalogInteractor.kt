package hse24.shop.catalog.mvi

import hse24.common.mvi.MviInteractor
import hse24.db.entity.toProductViewModel
import hse24.di.IoScheduler
import hse24.shop.catalog.di.CatalogScope
import hse24.shop.repository.CatalogRepository
import hse24.shop.usecase.catalog.FetchCatalogUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@CatalogScope
class CatalogInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val catalogRepository: CatalogRepository,
    private val fetchCatalogUseCase: FetchCatalogUseCase
) : MviInteractor<CatalogAction, CatalogResult> {

    private val initProcessor: ObservableTransformer<CatalogAction.Init, CatalogResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    catalogRepository
                        .observeProductsByCategory(it.categoryId)
                        .map { entities ->
                            CatalogResult.CatalogItems(entities.map { entity -> entity.toProductViewModel() }) as CatalogResult
                        }
                        .subscribeOn(ioScheduler)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CatalogResult.DataError
                        }
                }

        }

    private val fetchInitialCatalogProcessor: ObservableTransformer<CatalogAction.Init, CatalogResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    fetchCatalogUseCase.execute(true, it.categoryId)
                        .map { isLastPageReached ->
                            CatalogResult.FetchingFinished(isLastPageReached) as CatalogResult
                        }
                        .toObservable()
                        .subscribeOn(ioScheduler)
                        .startWith(CatalogResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CatalogResult.NetworkError
                        }
                }

        }

    private val fetchCatalogProcessor: ObservableTransformer<CatalogAction.LoadNextPage, CatalogResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    fetchCatalogUseCase.execute(false, it.categoryId)
                        .map { isLastPageReached ->
                            CatalogResult.FetchingFinished(isLastPageReached) as CatalogResult
                        }
                        .toObservable()
                        .subscribeOn(ioScheduler)
                        .startWith(CatalogResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CatalogResult.NetworkError
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
                                .compose(initProcessor),
                            shared.ofType(CatalogAction.Init::class.java)
                                .compose(fetchInitialCatalogProcessor),
                            shared.ofType(CatalogAction.LoadNextPage::class.java)
                                .compose(fetchCatalogProcessor)
                        )
                    )
                }
        }
}
