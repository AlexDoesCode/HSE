package hse24.shop.catalog.mvi

import hse24.common.mvi.*
import hse24.shop.catalog.adapter.CatalogItemViewModel

sealed class CatalogIntention : MviIntention {

    data class Init(val categoryId: Int) : CatalogIntention(), MviInitIntention
    data class LoadNextPage(val categoryId: Int) : CatalogIntention()
}

sealed class CatalogAction : MviAction {

    data class Init(val categoryId: Int) : CatalogAction()
    data class LoadNextPage(val categoryId: Int) : CatalogAction()
}

sealed class CatalogResult : MviResult {

    object Loading : CatalogResult()
    object DataError : CatalogResult()
    object NetworkError : CatalogResult()

    data class CatalogItems(val pageItems: List<CatalogItemViewModel>) : CatalogResult()
    data class FetchingFinished(val isLastPageReached: Boolean) : CatalogResult()
}

data class CatalogState(
    val isLoading: Boolean = false,
    val error: OneShot<CatalogError> = OneShot.empty(),
    val catalogItems: List<CatalogItemViewModel>? = null,
    val isLastPageReached: Boolean = false
) : ViewStateWithId(), MviState

enum class CatalogError {
    DATA, NETWORK
}
