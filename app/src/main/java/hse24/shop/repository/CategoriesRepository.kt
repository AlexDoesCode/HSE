package hse24.shop.repository

import hse24.db.dao.CategoriesDao
import hse24.db.entity.CategoryEntity
import hse24.db.entity.toCategoryModel
import hse24.di.IoScheduler
import hse24.network.ShoppingApi
import hse24.shop.categories.di.CategoriesScope
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

@CategoriesScope
class CategoriesRepository @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val shoppingApi: ShoppingApi,
    private val categoriesDao: CategoriesDao
) {

    fun observeDepartments(): Observable<List<CategoryModel>> =
        categoriesDao.observeDepartments()
            .observeOn(ioScheduler)
            .toObservable()
            .map { entities ->
                entities.map { it.toCategoryModel() }
            }

    fun getCategoriesByParentId(parentId: Int): Single<List<CategoryModel>> =
        categoriesDao.getCategoriesByParentId(parentId)
            .observeOn(ioScheduler)
            .map { entities ->
                entities.map { it.toCategoryModel() }
            }
            .toSingle(emptyList())

    fun getCategoriesWithChildren(departmentId: Int): Single<List<CategoryModel>> =
        categoriesDao.getCategoriesWithChildren(departmentId)
            .observeOn(ioScheduler)
            .map { entities ->
                entities.map { it.toCategoryModel() }
            }
            .toSingle(emptyList())


    fun getCategoriesWithoutChildren(departmentId: Int): Single<List<CategoryModel>> =
        categoriesDao.getCategoriesWithoutChildren(departmentId)
            .observeOn(ioScheduler)
            .map { entities ->
                entities.map { it.toCategoryModel() }
            }
            .toSingle(emptyList())

    fun fetchCategories() = shoppingApi.fetchCategories()

    fun persistCategories(categories: List<CategoryEntity>) =
        categoriesDao
            .insertAll(categories)
}
