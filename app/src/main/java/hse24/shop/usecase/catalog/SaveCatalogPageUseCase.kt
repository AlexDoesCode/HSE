package hse24.shop.usecase.catalog

import hse24.db.entity.toCatalogEntity
import hse24.network.model.ProductApiModel
import hse24.shop.catalog.di.CatalogScope
import hse24.shop.repository.CatalogRepository
import io.reactivex.Completable
import javax.inject.Inject

@CatalogScope
class SaveCatalogPageUseCase @Inject constructor(
    private val repository: CatalogRepository
) {

    fun execute(
        isFirstPage: Boolean,
        categoryId: Int,
        pageProducts: List<ProductApiModel>
    ): Completable =
        Completable.fromAction {
            when (isFirstPage) {
                true -> {
                    repository.persistProductsFromFirstPage(pageProducts.map {
                        it.toCatalogEntity(
                            categoryId
                        )
                    })
                }
                false -> {
                    repository.persistProductsPage(
                        pageProducts.map { it.toCatalogEntity(categoryId) })
                }
            }
        }
}
