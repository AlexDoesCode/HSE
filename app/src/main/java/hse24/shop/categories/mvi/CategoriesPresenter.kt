package hse24.shop.categories.mvi

import hse24.common.mvi.MviBasePresenter
import hse24.common.mvi.OneShot
import hse24.shop.categories.di.CategoriesScope
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@CategoriesScope
class CategoriesPresenter @Inject constructor(
    interactor: CategoriesInteractor
) : MviBasePresenter<CategoriesIntention, CategoriesAction, CategoriesResult, CategoriesState>(interactor) {

    override val defaultState: CategoriesState
        get() = CategoriesState()

    override fun actionFromIntention(intent: CategoriesIntention): CategoriesAction =
        when (intent) {
            CategoriesIntention.Init -> CategoriesAction.Init
            is CategoriesIntention.GetDepartmentCategories -> CategoriesAction.GetDepartmentCategories(intent.departmentId)
            is CategoriesIntention.GetSubcategories -> CategoriesAction.GetSubcategories(intent.categoryId)
            CategoriesIntention.ResetSubcategories -> CategoriesAction.ResetSubcategories
        }

    override val reducer: BiFunction<CategoriesState, CategoriesResult, CategoriesState>
        get() = BiFunction { prevState, result ->
            when (result) {
                CategoriesResult.NetworkError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(CategoryErrorType.Network)
                )
                CategoriesResult.DataError -> prevState.copy(
                    isLoading = false,
                    error = OneShot(CategoryErrorType.DataBase)
                )
                CategoriesResult.Loading -> prevState.copy(
                    isLoading = true
                )
                CategoriesResult.LoadingFinished -> prevState.copy(
                    isLoading = false
                )
                is CategoriesResult.Categories -> prevState.copy(
                    categories = result.models,
                    isLoading = false
                )
                is CategoriesResult.Departments -> prevState.copy(
                    departments = result.models,
                    isLoading = false
                )
                is CategoriesResult.Subcategories -> prevState.copy(
                    subCategories = result.models,
                    isLoading = false
                )
            }
        }
}
