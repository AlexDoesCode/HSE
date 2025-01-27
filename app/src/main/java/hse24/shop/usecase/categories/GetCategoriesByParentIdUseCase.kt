package hse24.shop.usecase.categories

import hse24.shop.categories.di.CategoriesScope
import hse24.shop.repository.CategoriesRepository
import hse24.shop.repository.CategoryModel
import io.reactivex.Single
import javax.inject.Inject

@CategoriesScope
class GetCategoriesByParentIdUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {

    fun execute(parentId: Int): Single<List<CategoryModel>> =
        categoriesRepository.getCategoriesByParentId(parentId)

}
