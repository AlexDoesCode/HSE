package hse24.shop.categories.mvi

import hse24.common.mvi.MviInteractor
import hse24.db.entity.toCategoryViewModel
import hse24.db.entity.toDepartmentViewModel
import hse24.db.entity.toSubcategoryViewModel
import hse24.di.IoScheduler
import hse24.shop.categories.adapter.CategoryItemViewModel
import hse24.shop.categories.di.CategoriesScope
import hse24.shop.repository.CategoriesRepository
import hse24.shop.repository.CategoryModel
import hse24.shop.usecase.categories.FetchCategoriesUseCase
import hse24.shop.usecase.categories.GetCategoriesByParentIdUseCase
import hse24.shop.usecase.categories.GetCategoriesWithChildrenUseCase
import hse24.shop.usecase.categories.GetCategoriesWithoutChildrenUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@CategoriesScope
class CategoriesInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val categoriesRepository: CategoriesRepository,
    private val fetchCategoriesUseCase: FetchCategoriesUseCase,
    private val getCategoriesByParentIdUseCase: GetCategoriesByParentIdUseCase,
    private val getCategoriesWithChildrenUseCase: GetCategoriesWithChildrenUseCase,
    private val getCategoriesWithoutChildrenUseCase: GetCategoriesWithoutChildrenUseCase
) : MviInteractor<CategoriesAction, CategoriesResult> {

    private val initProcessor: ObservableTransformer<CategoriesAction.Init, CategoriesResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    categoriesRepository
                        .observeDepartments()
                        .map {
                            CategoriesResult.Departments(it.map { item -> item.toDepartmentViewModel() }) as CategoriesResult
                        }
                        .subscribeOn(ioScheduler)
                        .startWith(CategoriesResult.Loading)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CategoriesResult.DataError
                        }
                }
        }

    private val fetchCategoriesTreeProcessor: ObservableTransformer<CategoriesAction.Init, CategoriesResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    fetchCategoriesUseCase.execute()
                        .andThen(
                            Observable.fromCallable {
                                CategoriesResult.LoadingFinished as CategoriesResult
                            }
                        )
                        .subscribeOn(ioScheduler)
                        .startWith(CategoriesResult.Loading)
                        .onErrorReturn { throwable ->
                            Timber.d(throwable)
                            CategoriesResult.NetworkError
                        }
                }
        }

    private val getCategoriesProcessor: ObservableTransformer<CategoriesAction.GetDepartmentCategories, CategoriesResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    Single.zip(
                        getCategoriesWithChildrenUseCase.execute(it.departmentId),
                        getCategoriesWithoutChildrenUseCase.execute(it.departmentId),
                        BiFunction<List<CategoryModel>, List<CategoryModel>, List<CategoryItemViewModel.CategoryViewModel>> { categoriesWithChildren, categoriesWithoutChildren ->
                            mutableListOf<CategoryItemViewModel.CategoryViewModel>()
                                .apply {
                                    addAll(categoriesWithChildren.map { item ->
                                        item.toCategoryViewModel(
                                            true
                                        )
                                    })
                                    addAll(categoriesWithoutChildren.map { item ->
                                        item.toCategoryViewModel(
                                            false
                                        )
                                    })
                                    sortBy { item -> item.name }
                                }
                        }
                    )
                        .map { viewModels ->
                            CategoriesResult.Categories(viewModels) as CategoriesResult
                        }
                        .subscribeOn(ioScheduler)
                        .toObservable()
                        .onErrorReturn { throwable ->
                            Timber.d(throwable)
                            CategoriesResult.DataError
                        }
                }
        }

    private val getSubcategoriesProcessor: ObservableTransformer<CategoriesAction.GetSubcategories, CategoriesResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    getCategoriesByParentIdUseCase.execute(it.categoryId)
                        .map { subcategoriesModels ->
                            CategoriesResult.Subcategories(
                                subcategoriesModels
                                    .map { item -> item.toSubcategoryViewModel() }
                                    .sortedBy { item -> item.name }) as CategoriesResult
                        }
                        .subscribeOn(ioScheduler)
                        .toObservable()
                        .onErrorReturn { throwable ->
                            Timber.d(throwable)
                            CategoriesResult.DataError
                        }
                }
        }

    private val resetSubcategoriesProcessor: ObservableTransformer<CategoriesAction.ResetSubcategories, CategoriesResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    Observable.just(
                        CategoriesResult.Subcategories(null) as CategoriesResult
                    )
                        .subscribeOn(ioScheduler)
                        .onErrorReturn { throwable ->
                            Timber.d(throwable)
                            CategoriesResult.DataError
                        }
                }
        }

    override fun actionProcessor(): ObservableTransformer<in CategoriesAction, out CategoriesResult> =
        ObservableTransformer { action ->
            action
                .observeOn(ioScheduler)
                .publish { shared ->
                    Observable.merge(
                        listOf(
                            shared.ofType(CategoriesAction.Init::class.java)
                                .compose(initProcessor),
                            shared.ofType(CategoriesAction.Init::class.java)
                                .compose(fetchCategoriesTreeProcessor),
                            shared.ofType(CategoriesAction.GetDepartmentCategories::class.java)
                                .compose(getCategoriesProcessor),
                            shared.ofType(CategoriesAction.GetSubcategories::class.java)
                                .compose(getSubcategoriesProcessor),
                            shared.ofType(CategoriesAction.ResetSubcategories::class.java)
                                .compose(resetSubcategoriesProcessor),
                            shared.ofType(CategoriesAction.ResetCategories::class.java)
                                .map { CategoriesResult.ResetCategories }
                        )
                    )
                }
        }
}
