package hse24.shop.usecase.catalog

import hse24.shop.catalog.di.CatalogScope
import io.reactivex.Single
import javax.inject.Inject

@CatalogScope
class FetchCatalogUseCase @Inject constructor(
    private val loadCatalogByPageUseCase: LoadCatalogByPageUseCase,
    private val saveCatalogPageUseCase: SaveCatalogPageUseCase
) {

    //Fetches catalog by category and returns pair: if the last page was reached and if the category is empty
    fun execute(isFirstPage: Boolean, categoryId: Int): Single<Pair<Boolean, Boolean>> =
        loadCatalogByPageUseCase.execute(isFirstPage, categoryId)
            .flatMap { isLastPageReachedProductsPair ->
                saveCatalogPageUseCase.execute(
                    isFirstPage,
                    categoryId,
                    isLastPageReachedProductsPair.second
                )
                    .toSingleDefault(
                        Pair(
                            isLastPageReachedProductsPair.first,
                            isLastPageReachedProductsPair.second.isNullOrEmpty()
                        )
                    )
            }
}
