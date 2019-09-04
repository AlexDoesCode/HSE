package hse24.shop.cart

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hse24.common.extension.addTo
import hse24.shop.cart.mvi.CartAction
import hse24.shop.cart.mvi.CartInteractor
import hse24.shop.cart.mvi.CartResult
import hse24.shop.repository.CartRepository
import hse24.shop.usecase.cart.RemoveCartItemUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class CartInteractorTest {

    private companion object {
        const val TEST_SKU = 0
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val scheduler = Schedulers.trampoline()
    @Mock
    private lateinit var cartRepository: CartRepository
    @Mock
    private lateinit var removeCartItemUseCase: RemoveCartItemUseCase
    @Mock
    private lateinit var throwableMock: Throwable

    private lateinit var disposables: CompositeDisposable
    private lateinit var interactor: CartInteractor

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
        whenever(cartRepository.observeCart())
            .thenReturn(Observable.just(emptyList()))

        val testObserver = createInteractorActionProcessor(CartAction.Init)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CartResult.CartItems(emptyList())
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(cartRepository, times(1)).observeCart()
        verifyZeroInteractions(
            removeCartItemUseCase
        )
    }

    @Test
    fun `when init action error produce correct result`() {
        whenever(cartRepository.observeCart())
            .thenReturn(Observable.error(throwableMock))

        val testObserver = createInteractorActionProcessor(CartAction.Init)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            CartResult.Error
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(cartRepository, times(1)).observeCart()
        verifyZeroInteractions(
            removeCartItemUseCase
        )
    }

    @Test
    fun `when remove cart item action produce correct result`() {
        whenever(removeCartItemUseCase.execute(TEST_SKU))
            .thenReturn(Completable.complete())

        val testObserver = createInteractorActionProcessor(CartAction.RemoveAction(TEST_SKU))
            .test()
            .addTo(disposables)

        testObserver.run {
            assertNoValues()
            assertNoErrors()
        }

        verify(removeCartItemUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            cartRepository
        )
    }

    @Test
    fun `when remove cart action error produce correct result`() {
        whenever(removeCartItemUseCase.execute(TEST_SKU))
            .thenReturn(Completable.error(throwableMock))

        val testObserver = createInteractorActionProcessor(CartAction.RemoveAction(TEST_SKU))
            .test()
            .addTo(disposables)

        testObserver.run {
            assertNoErrors()
        }

        verify(removeCartItemUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            cartRepository
        )
    }

    // region Helpers

    private fun createInteractor(): CartInteractor =
        CartInteractor(
            scheduler,
            cartRepository,
            removeCartItemUseCase

        )

    private fun createInteractorActionProcessor(action: CartAction): Observable<CartResult> =
        Observable.fromCallable { action }.compose(interactor.actionProcessor())

    // endregion
}
