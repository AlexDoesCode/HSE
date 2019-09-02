package hse24.shop.categories.mvi

import hse24.common.mvi.*
import hse24.shop.categories.adapter.CategoryItemViewModel
import hse24.shop.categories.adapter.DepartmentViewModel

sealed class CategoriesIntention : MviIntention {

    object Init : CategoriesIntention(), MviInitIntention
    object ResetSubcategories : CategoriesIntention()

    data class GetDepartmentCategories(val departmentId: Int) : CategoriesIntention()
    data class GetSubcategories(val categoryId: Int) : CategoriesIntention()
}

sealed class CategoriesAction : MviAction {

    object Init : CategoriesAction()
    object ResetSubcategories : CategoriesAction()

    data class GetDepartmentCategories(val departmentId: Int) : CategoriesAction()
    data class GetSubcategories(val categoryId: Int) : CategoriesAction()
}

sealed class CategoriesResult : MviResult {

    object Loading : CategoriesResult()
    object LoadingFinished : CategoriesResult()
    object NetworkError : CategoriesResult()
    object DataError : CategoriesResult()

    data class Departments(val models: List<DepartmentViewModel>) : CategoriesResult()
    data class Categories(val models: List<CategoryItemViewModel.CategoryViewModel>) : CategoriesResult()
    data class Subcategories(val models: List<CategoryItemViewModel.SubcategoryViewModel>?) : CategoriesResult()
}

data class CategoriesState(
    val isLoading: Boolean = false,
    val error: OneShot<CategoryErrorType> = OneShot.empty(),
    val departments: List<DepartmentViewModel>? = null,
    val categories: List<CategoryItemViewModel>? = null,
    val subCategories: List<CategoryItemViewModel>? = null
) : ViewStateWithId(), MviState

enum class CategoryErrorType {
    Network,
    DataBase
}
