package hse24.shop.catalog.mvi

import hse24.common.mvi.*
import hse24.shop.catalog.adapter.CatalogItemViewModel

sealed class CatalogIntention : MviIntention {

    object Init : CatalogIntention(), MviInitIntention

    data class LoadNextPage(val pageIndex: Int) : CatalogIntention()
}

sealed class CatalogAction : MviAction {

    object Init : CatalogAction()

    data class LoadNextPage(val pageIndex: Int) : CatalogAction()

}

sealed class CatalogResult : MviResult {

    object Loading : CatalogResult()
    object Error : CatalogResult()

    data class CatalogItems(val pageIndex: Int, val pageItems: List<CatalogItemViewModel>) : CatalogResult()

}

data class CatalogState(
    val isLoading: Boolean = false,
    val error: OneShot<Boolean> = OneShot.empty(),
    val catalogItems: List<CatalogItemViewModel>? = null,
    val isLastPage: OneShot<Boolean> = OneShot.empty()
) : ViewStateWithId(), MviState
