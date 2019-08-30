package hse24.shop.catalog.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.catalog.di.CatalogScope
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@CatalogScope
class CatalogPresenter @Inject constructor(
    interactor: CatalogInteractor
) : MviBasePresenter<CatalogIntention, CatalogAction, CatalogResult, CatalogState>(interactor) {

    override val defaultState: CatalogState
        get() = CatalogState()

    override fun actionFromIntention(intent: CatalogIntention): CatalogAction =
        when (intent) {
            CatalogIntention.Init -> CatalogAction.Init
            is CatalogIntention.LoadNextPage -> CatalogAction.LoadNextPage(intent.pageIndex)
        }

    override val reducer: BiFunction<CatalogState, CatalogResult, CatalogState>
        get() = BiFunction { prevState, result ->
            when (result) {
                CatalogResult.Error -> prevState.copy(
                    isLoading = false,
                    error = OneShot(true)
                )
                CatalogResult.Loading -> prevState.copy(
                    isLoading = true
                )
                is CatalogResult.CatalogItems -> prevState.copy(
                    isLoading = false,
                    isLastPage = OneShot(false),
                    catalogItems = result.pageItems
                )
            }
        }
}
