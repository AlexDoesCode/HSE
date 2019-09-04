package hse24.shop.catalog

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import hse24.common.extension.addTo
import hse24.common.mvi.OneShot
import hse24.shop.catalog.mvi.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test

class CatalogPresenterTest {

    private companion object {
        const val TEST_ID = 0

    }

    private lateinit var disposables: CompositeDisposable
    private lateinit var presenter: CatalogPresenter

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
        presenter = createPresenter { CatalogResult.DataError }

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
            Observable.just(CatalogIntention.Init(TEST_ID))
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
            Observable.just(CatalogIntention.Init(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when load more intention produces correct state`() {
        val (result, state) = createLoadMorePair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CatalogIntention.LoadNextPage(TEST_ID))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when load more intention produces error`() {
        val (result, state) = createLoadMorerrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CatalogIntention.LoadNextPage(TEST_ID))
        )
            .addTo(disposables)

        testObserver.awaitTerminalEvent()
        testObserver.assertValue(state)
    }

    //endregion

    // region Helpers

    private fun createPresenter(actionMapper: (CatalogAction) -> CatalogResult): CatalogPresenter {
        val interactor = mock<CatalogInteractor> {
            on { actionProcessor() } doReturn ObservableTransformer {
                it.map { actionMapper(it) }
            }
        }
        return CatalogPresenter(interactor)
    }

    private fun createInitialPair(): Pair<CatalogResult.CatalogItems, CatalogState> {
        val result = CatalogResult.CatalogItems(emptyList())
        val state = CatalogState(catalogItems = emptyList())
        return result to state
    }

    private fun createInitialErrorPair(): Pair<CatalogResult.NetworkError, CatalogState> {
        val result = CatalogResult.NetworkError
        val state = CatalogState(error = OneShot(CatalogError.NETWORK))
        return result to state
    }

    private fun createLoadMorePair(): Pair<CatalogResult.CatalogItems, CatalogState> {
        val result = CatalogResult.CatalogItems(emptyList())
        val state = CatalogState(catalogItems = emptyList())
        return result to state
    }

    private fun createLoadMorerrorPair(): Pair<CatalogResult.NetworkError, CatalogState> {
        val result = CatalogResult.NetworkError
        val state = CatalogState(error = OneShot(CatalogError.NETWORK))
        return result to state
    }

    //endregion

}
