package hse24.shop.usecase.categories

import hse24.network.model.CategoryApiModel
import hse24.shop.categories.di.CategoriesScope
import hse24.shop.repository.CategoriesRepository
import hse24.shop.repository.util.CategoriesTree
import io.reactivex.Completable
import javax.inject.Inject

@CategoriesScope
class SaveCategoriesUseCase @Inject constructor(
    private val catalogRepository: CategoriesRepository
) {

    fun execute(categoryModel: CategoryApiModel) =
        Completable.fromAction {
            catalogRepository.persistCategories(
                CategoriesTree(categoryModel)
                    .items
            )
        }
}
