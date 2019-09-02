package hse24.shop.usecase.categories

import hse24.shop.categories.di.CategoriesScope
import hse24.shop.repository.CategoriesRepository
import io.reactivex.Completable
import javax.inject.Inject

@CategoriesScope
class FetchCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository,
    private val saveCategoriesUseCase: SaveCategoriesUseCase
) {

    fun execute(): Completable =
        repository.fetchCategories()
            .flatMapCompletable { root ->
                saveCategoriesUseCase.execute(root)
            }
}
