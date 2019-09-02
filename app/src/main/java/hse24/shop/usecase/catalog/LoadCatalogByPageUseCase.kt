package hse24.shop.usecase.catalog

import hse24.shop.catalog.di.CatalogScope
import hse24.shop.repository.CatalogRepository
import javax.inject.Inject

@CatalogScope
class LoadCatalogByPageUseCase @Inject constructor(
    private val repository: CatalogRepository
) {

    fun execute(isFirstPage: Boolean, categoryId: Int) =
        repository.fetchCatalogItemsById(isFirstPage, categoryId)
}
