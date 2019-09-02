package hse24.shop.usecase.categories

import hse24.network.ShoppingApi
import io.reactivex.Completable
import javax.inject.Inject

class FetchCategoriesUseCase @Inject constructor(
    private val shoppingApi: ShoppingApi,
    private val saveCategoriesUseCase: SaveCategoriesUseCase
) {

    fun execute(): Completable =
        shoppingApi.fetchCategories()
            .flatMapCompletable { root ->
                saveCategoriesUseCase.execute(root)
            }
}
