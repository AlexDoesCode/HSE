package hse24.shop.catalog.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.catalog.adapter.CatalogItemViewModel
import hse24.shop.catalog.di.CatalogScope
import hse24.shop.repository.CatalogRepository.Companion.DEFAULT_ITEMS_PER_PAGE_AMOUNT
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
            is CatalogIntention.Init -> CatalogAction.Init(intent.categoryId)
            is CatalogIntention.LoadNextPage -> CatalogAction.LoadNextPage(intent.categoryId)
        }

    override val reducer: BiFunction<CatalogState, CatalogResult, CatalogState>
        get() = BiFunction { prevState, result ->
            when (result) {
                CatalogResult.Loading -> prevState.copy(
                    isLoading = true
                )
                is CatalogResult.CatalogItems -> {
                    val items =
                        if (prevState.isLastPageReached.not() && result.pageItems.size > DEFAULT_ITEMS_PER_PAGE_AMOUNT) {
                            mutableListOf<CatalogItemViewModel>()
                                .apply {
                                    addAll(result.pageItems)
                                    add(CatalogItemViewModel.LoadMore)
                                }
                        } else {
                            result.pageItems
                        }
                    prevState.copy(
                        isLoading = false,
                        catalogItems = items
                    )
                }
                CatalogResult.DataError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(CatalogError.DATA)
                )
                CatalogResult.NetworkError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(CatalogError.NETWORK)
                )
                is CatalogResult.FetchingFinished -> {
                    when (result.isCategoryEmpty) {
                        true -> prevState.copy(
                            isLoading = false,
                            isLastPageReached = true,
                            catalogItems = listOf(CatalogItemViewModel.Empty)
                        )
                        else -> prevState.copy(
                            isLoading = false,
                            isLastPageReached = result.isLastPageReached
                        )
                    }
                }
            }
        }
}
