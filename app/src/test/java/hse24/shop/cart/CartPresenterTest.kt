package hse24.shop.cart

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import hse24.common.extension.addTo
import hse24.common.extension.toCurrency
import hse24.common.mvi.OneShot
import hse24.shop.cart.adapter.CartItemViewModel
import hse24.shop.cart.mvi.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test

class CartPresenterTest {

    private companion object {
        const val TEST_SKU = 0

        val testViewModelList = listOf<CartItemViewModel>(
            CartItemViewModel.ProductItem(
                id = 0,
                sku = 0,
                brandName = "GUCCI",
                name = "Gang",
                variation = "Gangsta",
                imageUrl = "non-existent",
                price = 313.37f,
                currency = "USD".toCurrency()
            )
        )
    }

    private lateinit var disposables: CompositeDisposable
    private lateinit var presenter: CartPresenter

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
        presenter = createPresenter { CartResult.Error }

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
            Observable.just(CartIntention.Init)
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
            Observable.just(CartIntention.Init)
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when remove item intention produces correct state`() {
        val (result, state) = createRemoveItemsPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CartIntention.RemoveItem(TEST_SKU))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    @Test
    fun `when get currencies intention produces error`() {
        val (result, state) = createRemoveItemErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(CartIntention.RemoveItem(TEST_SKU))
        )
            .addTo(disposables)
        testObserver.assertValue(state)
    }

    //endregion

    // region Helpers

    private fun createPresenter(actionMapper: (CartAction) -> CartResult): CartPresenter {
        val interactor = mock<CartInteractor> {
            on { actionProcessor() } doReturn ObservableTransformer {
                it.map { actionMapper(it) }
            }
        }
        return CartPresenter(interactor)
    }

    private fun createInitialPair(): Pair<CartResult.CartItems, CartState> {
        val result = CartResult.CartItems(emptyList())
        val state = CartState(cartItems = listOf(CartItemViewModel.EmptyCart))
        return result to state
    }

    private fun createInitialErrorPair(): Pair<CartResult.Error, CartState> {
        val result = CartResult.Error
        val state = CartState(error = OneShot(true))
        return result to state
    }

    private fun createRemoveItemsPair(): Pair<CartResult.CartItems, CartState> {
        val result = CartResult.CartItems(testViewModelList)
        val state = CartState(cartItems = mutableListOf<CartItemViewModel>()
            .apply {
                addAll(testViewModelList)
                val model = testViewModelList.first() as CartItemViewModel.ProductItem
                add(CartItemViewModel.OverallSum(model.price, model.currency))
            }
        )
        return result to state
    }

    private fun createRemoveItemErrorPair(): Pair<CartResult.Error, CartState> {
        val result = CartResult.Error
        val state = CartState(error = OneShot(true))
        return result to state
    }

    //endregion

}
