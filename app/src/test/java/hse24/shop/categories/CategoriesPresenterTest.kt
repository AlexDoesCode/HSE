package hse24.shop.categories

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import hse24.common.extension.addTo
import hse24.common.mvi.OneShot
import hse24.shop.categories.mvi.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test

class CategoriesPresenterTest {

    private companion object {
        const val TEST_ID = 0

    }

    private lateinit var disposables: CompositeDisposable
    private lateinit var presenter: CategoriesPresenter

    @Before
    fun setUp() {
        disposables = CompositeDisposable()
    }

    @After
    fun tearDown() {
        disposables.dispose()
    }

    // region Tests

    @Test
    fun `when states subscribed no result returned`() {
        presenter = createPresenter { CategoriesResult.DataError }

        presenter.states()
            .test()
            .assertNoValues()
            .addTo(disposables)
    }

    @Test
    fun `when initial intention produces correct state`() {
        val (result, state) = createInitialPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.Init)
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    @Test
    fun `when initial intention produces error`() {
        val (result, state) = createInitialErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.Init)
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when department categories intention produces correct state`() {
        val (result, state) = createGetDepartmentCategoriesPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.GetDepartmentCategories(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when department categories intention fails produces correct state`() {
        val (result, state) = createGetDepartmentCategoriesErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.GetDepartmentCategories(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }


    @Test
    fun `when subcategories intention produces correct state`() {
        val (result, state) = createGetSubcategoriesPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.GetSubcategories(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when subcategories intention fails produces correct state`() {
        val (result, state) = createGetSubcategoriesErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CategoriesIntention.GetSubcategories(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    //endregion

    // region Helpers

    private fun createPresenter(actionMapper: (CategoriesAction) -> CategoriesResult): CategoriesPresenter {
        val interactor = mock<CategoriesInteractor> {
            on { actionProcessor() } doReturn ObservableTransformer {
                it.map { actionMapper(it) }
            }
        }
        return CategoriesPresenter(interactor)
    }

    private fun createInitialPair(): Pair<CategoriesResult.Departments, CategoriesState> {
        val result = CategoriesResult.Departments(emptyList())
        val state = CategoriesState(departments = emptyList())
        return result to state
    }

    private fun createInitialErrorPair(): Pair<CategoriesResult.NetworkError, CategoriesState> {
        val result = CategoriesResult.NetworkError
        val state = CategoriesState(error = OneShot(CategoryErrorType.Network))
        return result to state
    }

    private fun createGetDepartmentCategoriesPair(): Pair<CategoriesResult.Categories, CategoriesState> {
        val result = CategoriesResult.Categories(emptyList())
        val state = CategoriesState(categories = emptyList())
        return result to state
    }

    private fun createGetDepartmentCategoriesErrorPair(): Pair<CategoriesResult.DataError, CategoriesState> {
        val result = CategoriesResult.DataError
        val state = CategoriesState(error = OneShot(CategoryErrorType.DataBase))
        return result to state
    }

    private fun createGetSubcategoriesPair(): Pair<CategoriesResult.Subcategories, CategoriesState> {
        val result = CategoriesResult.Subcategories(emptyList())
        val state = CategoriesState(subCategories = emptyList())
        return result to state
    }

    private fun createGetSubcategoriesErrorPair(): Pair<CategoriesResult.DataError, CategoriesState> {
        val result = CategoriesResult.DataError
        val state = CategoriesState(error = OneShot(CategoryErrorType.DataBase))
        return result to state
    }

    //endregion

}
