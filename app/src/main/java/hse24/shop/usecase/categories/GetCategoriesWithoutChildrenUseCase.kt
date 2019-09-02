package hse24.shop.usecase.categories

import hse24.shop.repository.CategoriesRepository
import javax.inject.Inject

class GetCategoriesWithoutChildrenUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {

    fun execute(departmentId: Int) =
        categoriesRepository.getCategoriesWithoutChildren(departmentId)
}
