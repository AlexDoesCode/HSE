package hse24.shop.categories

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hse24.common.extension.addTo
import hse24.shop.categories.mvi.CategoriesAction
import hse24.shop.categories.mvi.CategoriesInteractor
import hse24.shop.categories.mvi.CategoriesResult
import hse24.shop.repository.CategoriesRepository
import hse24.shop.usecase.categories.FetchCategoriesUseCase
import hse24.shop.usecase.categories.GetCategoriesByParentIdUseCase
import hse24.shop.usecase.categories.GetCategoriesWithChildrenUseCase
import hse24.shop.usecase.categories.GetCategoriesWithoutChildrenUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class CategoriesInteractorTest {

    private companion object {
        const val TEST_ID = 0
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val scheduler = Schedulers.trampoline()
    @Mock
    private lateinit var categoriesRepository: CategoriesRepository
    @Mock
    private lateinit var fetchCategoriesUseCase: FetchCategoriesUseCase
    @Mock
    private lateinit var getCategoriesByParentIdUseCase: GetCategoriesByParentIdUseCase
    @Mock
    private lateinit var getCategoriesWithChildrenUseCase: GetCategoriesWithChildrenUseCase
    @Mock
    private lateinit var getCategoriesWithoutChildrenUseCase: GetCategoriesWithoutChildrenUseCase
    @Mock
    private lateinit var throwableMock: Throwable

    private lateinit var disposables: CompositeDisposable
    private lateinit var interactor: CategoriesInteractor

    @Before
    fun setUp() {
        disposables = CompositeDisposable()
        interactor = createInteractor()
    }

    @After
    fun tearDown() {
        disposables.dispose()
    }

    // region Tests

    @Test
    fun `when init action produce correct result`() {
        whenever(categoriesRepository.observeDepartments())
            .thenReturn(Observable.just(emptyList()))
        whenever(fetchCategoriesUseCase.execute())
            .thenReturn(Completable.complete())

        val testObserver = createInteractorActionProcessor(CategoriesAction.Init)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.Loading,
            CategoriesResult.LoadingFinished,
            CategoriesResult.Departments(emptyList())

        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(categoriesRepository, times(1)).observeDepartments()
        verify(fetchCategoriesUseCase, times(1)).execute()
        verifyZeroInteractions(
            getCategoriesByParentIdUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )
    }

    @Test
    fun `when init action fetch error produce correct result`() {
        whenever(categoriesRepository.observeDepartments())
            .thenReturn(Observable.just(emptyList()))
        whenever(fetchCategoriesUseCase.execute())
            .thenReturn(Completable.error(throwableMock))

        val testObserver = createInteractorActionProcessor(CategoriesAction.Init)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.Departments(emptyList()),
            CategoriesResult.Loading,
            CategoriesResult.NetworkError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(categoriesRepository, times(1)).observeDepartments()
        verify(fetchCategoriesUseCase, times(1)).execute()
        verifyZeroInteractions(
            getCategoriesByParentIdUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )
    }

    @Test
    fun `when reset categories action produce correct result`() {
        val testObserver = createInteractorActionProcessor(CategoriesAction.ResetSubcategories)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.Subcategories(null)
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesByParentIdUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )
    }

    @Test
    fun `when get department categories action produce correct result`() {
        whenever(getCategoriesWithChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.just(emptyList()))
        whenever(getCategoriesWithoutChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.just(emptyList()))

        val testObserver =
            createInteractorActionProcessor(CategoriesAction.GetDepartmentCategories(TEST_ID))
                .test()
                .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.Categories(emptyList())
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(getCategoriesWithChildrenUseCase, times(1)).execute(TEST_ID)
        verify(getCategoriesWithoutChildrenUseCase, times(1)).execute(TEST_ID)
        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesByParentIdUseCase
        )
    }

    @Test
    fun `when get department categories action categories with children produce correct result`() {
        whenever(getCategoriesWithChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.error(throwableMock))
        whenever(getCategoriesWithoutChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.just(emptyList()))

        val testObserver =
            createInteractorActionProcessor(CategoriesAction.GetDepartmentCategories(TEST_ID))
                .test()
                .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.DataError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(getCategoriesWithChildrenUseCase, times(1)).execute(TEST_ID)
        verify(getCategoriesWithoutChildrenUseCase, times(1)).execute(TEST_ID)
        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesByParentIdUseCase
        )
    }

    @Test
    fun `when get department categories action categories without children produce correct result`() {
        whenever(getCategoriesWithChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.just(emptyList()))
        whenever(getCategoriesWithoutChildrenUseCase.execute(TEST_ID))
            .thenReturn(Single.error(throwableMock))

        val testObserver =
            createInteractorActionProcessor(CategoriesAction.GetDepartmentCategories(TEST_ID))
                .test()
                .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.DataError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(getCategoriesWithChildrenUseCase, times(1)).execute(TEST_ID)
        verify(getCategoriesWithoutChildrenUseCase, times(1)).execute(TEST_ID)
        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesByParentIdUseCase
        )
    }

    @Test
    fun `when get subcategories action produce correct result`() {
        whenever(getCategoriesByParentIdUseCase.execute(TEST_ID))
            .thenReturn(Single.just(emptyList()))

        val testObserver =
            createInteractorActionProcessor(CategoriesAction.GetSubcategories(TEST_ID))
                .test()
                .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.Subcategories(emptyList())
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(getCategoriesByParentIdUseCase, times(1)).execute(TEST_ID)
        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )
    }


    @Test
    fun `when get subcategories action error produce correct result`() {
        whenever(getCategoriesByParentIdUseCase.execute(TEST_ID))
            .thenReturn(Single.error(throwableMock))

        val testObserver =
            createInteractorActionProcessor(CategoriesAction.GetSubcategories(TEST_ID))
                .test()
                .addTo(disposables)

        val expectedResults = listOf(
            CategoriesResult.DataError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(getCategoriesByParentIdUseCase, times(1)).execute(TEST_ID)
        verifyZeroInteractions(
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )
    }

    // region Helpers

    private fun createInteractor(): CategoriesInteractor =
        CategoriesInteractor(
            scheduler,
            categoriesRepository,
            fetchCategoriesUseCase,
            getCategoriesByParentIdUseCase,
            getCategoriesWithChildrenUseCase,
            getCategoriesWithoutChildrenUseCase
        )

    private fun createInteractorActionProcessor(action: CategoriesAction): Observable<CategoriesResult> =
        Observable.fromCallable { action }.compose(interactor.actionProcessor())

    // endregion
}
