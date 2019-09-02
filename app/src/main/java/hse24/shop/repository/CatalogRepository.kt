package hse24.shop.repository

import hse24.db.dao.CatalogDao
import hse24.db.entity.CatalogEntity
import hse24.di.IoScheduler
import hse24.network.ShoppingApi
import hse24.shop.catalog.di.CatalogScope
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject

@CatalogScope
class CatalogRepository @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val shoppingApi: ShoppingApi,
    private val catalogDao: CatalogDao
) {

    companion object {
        const val DEFAULT_ITEMS_PER_PAGE_AMOUNT = 24
    }

    private var currentPageIndex = 1

    fun observeProductsByCategory(categoryId: Int): Observable<List<CatalogEntity>> =
        catalogDao.observeProductsByCategoryId(categoryId)
            .observeOn(ioScheduler)
            .toObservable()

    //Returns pair (isLastPageReached + products for the current page)
    fun fetchCatalogItemsById(isFirstPage: Boolean, categoryId: Int) = when (isFirstPage) {
        true -> shoppingApi.fetchCategoryCatalog(categoryId, 1)
        false -> shoppingApi.fetchCategoryCatalog(categoryId, currentPageIndex)
    }.map {
        currentPageIndex = it.paging.page + 1
        Pair(it.paging.page == it.paging.numPages, it.productResults)
    }

    fun persistProductsPage(products: List<CatalogEntity>) =
        catalogDao.insertAll(products)

    fun persistProductsFromFirstPage(products: List<CatalogEntity>) =
        catalogDao
            .insertFromFirstPage(products)
}
