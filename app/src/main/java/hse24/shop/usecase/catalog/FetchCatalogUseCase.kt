package hse24.shop.usecase.catalog

import hse24.shop.catalog.di.CatalogScope
import io.reactivex.Single
import javax.inject.Inject

@CatalogScope
class FetchCatalogUseCase @Inject constructor(
    private val loadCatalogByPageUseCase: LoadCatalogByPageUseCase,
    private val saveCatalogPageUseCase: SaveCatalogPageUseCase
) {

    //Fetches catalog by category and returns if the last page was reached
    fun execute(isFirstPage: Boolean, categoryId: Int): Single<Boolean> =
        loadCatalogByPageUseCase.execute(isFirstPage, categoryId)
            .flatMap { isLastPageReachedProductsPair ->
                saveCatalogPageUseCase.execute(
                    isFirstPage,
                    categoryId,
                    isLastPageReachedProductsPair.second
                )
                    .toSingleDefault(isLastPageReachedProductsPair.first)
            }
}
