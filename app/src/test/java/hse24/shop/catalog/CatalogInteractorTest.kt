package hse24.shop.catalog

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hse24.common.extension.addTo
import hse24.shop.catalog.mvi.CatalogAction
import hse24.shop.catalog.mvi.CatalogInteractor
import hse24.shop.catalog.mvi.CatalogResult
import hse24.shop.repository.CatalogRepository
import hse24.shop.usecase.catalog.FetchCatalogUseCase
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

class CatalogInteractorTest {

    private companion object {
        const val TEST_ID = 0
        const val IS_LAST_PAGE_REACHED = true
        const val IS_CATEGORY_EMPTY = true
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val scheduler = Schedulers.trampoline()
    @Mock
    private lateinit var catalogRepository: CatalogRepository
    @Mock
    private lateinit var fetchCatalogUseCase: FetchCatalogUseCase
    @Mock
    private lateinit var throwableMock: Throwable

    private lateinit var disposables: CompositeDisposable
    private lateinit var interactor: CatalogInteractor

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
        whenever(catalogRepository.observeProductsByCategory(TEST_ID))
            .thenReturn(Observable.just(emptyList()))
        whenever(fetchCatalogUseCase.execute(true, TEST_ID))
            .thenReturn(Single.just(Pair(IS_LAST_PAGE_REACHED, IS_CATEGORY_EMPTY)))

        val testObserver = createInteractorActionProcessor(CatalogAction.Init(TEST_ID))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CatalogResult.CatalogItems(emptyList()),
            CatalogResult.Loading,
            CatalogResult.FetchingFinished(IS_LAST_PAGE_REACHED, IS_CATEGORY_EMPTY)
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(catalogRepository, times(1)).observeProductsByCategory(TEST_ID)
        verify(fetchCatalogUseCase, times(1)).execute(true, TEST_ID)
    }

    @Test
    fun `when init action fetch error produce correct result`() {
        whenever(catalogRepository.observeProductsByCategory(TEST_ID))
            .thenReturn(Observable.just(emptyList()))
        whenever(fetchCatalogUseCase.execute(true, TEST_ID))
            .thenReturn(Single.error(throwableMock))

        val testObserver = createInteractorActionProcessor(CatalogAction.Init(TEST_ID))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CatalogResult.CatalogItems(emptyList()),
            CatalogResult.Loading,
            CatalogResult.NetworkError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(catalogRepository, times(1)).observeProductsByCategory(TEST_ID)
        verify(fetchCatalogUseCase, times(1)).execute(true, TEST_ID)
    }

    @Test
    fun `when load action produce correct result`() {
        whenever(fetchCatalogUseCase.execute(false, TEST_ID))
            .thenReturn(Single.just(Pair(IS_LAST_PAGE_REACHED, IS_CATEGORY_EMPTY)))

        val testObserver = createInteractorActionProcessor(CatalogAction.LoadNextPage(TEST_ID))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CatalogResult.Loading,
            CatalogResult.FetchingFinished(IS_LAST_PAGE_REACHED, IS_CATEGORY_EMPTY)
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchCatalogUseCase, times(1)).execute(false, TEST_ID)
        verifyZeroInteractions(
            catalogRepository
        )
    }

    @Test
    fun `when load action error produce correct result`() {
        whenever(fetchCatalogUseCase.execute(false, TEST_ID))
            .thenReturn(Single.error(throwableMock))

        val testObserver = createInteractorActionProcessor(CatalogAction.LoadNextPage(TEST_ID))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CatalogResult.Loading,
            CatalogResult.NetworkError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchCatalogUseCase, times(1)).execute(false, TEST_ID)
        verifyZeroInteractions(
            catalogRepository
        )
    }

    // region Helpers

    private fun createInteractor(): CatalogInteractor =
        CatalogInteractor(
            scheduler,
            catalogRepository,
            fetchCatalogUseCase
        )

    private fun createInteractorActionProcessor(action: CatalogAction): Observable<CatalogResult> =
        Observable.fromCallable { action }.compose(interactor.actionProcessor())

    // endregion
}
