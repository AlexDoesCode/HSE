package hse24.shop.usecase.catalog

import hse24.shop.catalog.di.CatalogScope
import hse24.shop.repository.CatalogRepository
import javax.inject.Inject

@CatalogScope
class LoadCatalogNextPageUseCase @Inject constructor(
    repository: CatalogRepository,
    saveCatalogPageUseCase: SaveCatalogPageUseCase
) {

    fun execute() {

    }
}
