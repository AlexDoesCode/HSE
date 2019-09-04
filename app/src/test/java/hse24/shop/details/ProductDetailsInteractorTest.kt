package hse24.shop.details

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hse24.common.extension.addTo
import hse24.db.entity.toProductDetailsViewModel
import hse24.shop.details.mvi.ProductDetailsAction
import hse24.shop.details.mvi.ProductDetailsInteractor
import hse24.shop.details.mvi.ProductDetailsResult
import hse24.shop.repository.ProductDetailsModel
import hse24.shop.usecase.details.AddProductToCartUseCase
import hse24.shop.usecase.details.FetchProductBySkuUseCase
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

class ProductDetailsInteractorTest {

    private companion object {
        const val TEST_SKU = 0

        val dummyProductDetailsModel = ProductDetailsModel(
            sku = TEST_SKU,
            imageUris = emptyList(),
            title = "",
            name = "",
            brandName = "",
            description = "",
            variationName = "UNKNOWN ",
            price = 0f,
            currency = "",
            variations = emptyList()
        )
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val scheduler = Schedulers.trampoline()
    @Mock
    private lateinit var fetchProductBySkuUseCase: FetchProductBySkuUseCase
    @Mock
    private lateinit var addProductToCartUseCase: AddProductToCartUseCase
    @Mock
    private lateinit var throwableMock: Throwable

    private lateinit var disposables: CompositeDisposable
    private lateinit var interactor: ProductDetailsInteractor

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
        whenever(fetchProductBySkuUseCase.execute(TEST_SKU))
            .thenReturn(Single.just(dummyProductDetailsModel))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.Init(TEST_SKU))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.Loading,
            ProductDetailsResult.ProductData(viewModel = dummyProductDetailsModel.toProductDetailsViewModel())
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchProductBySkuUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            addProductToCartUseCase
        )
    }

    @Test
    fun `when init action error produce correct result`() {
        whenever(fetchProductBySkuUseCase.execute(TEST_SKU))
            .thenReturn(Single.error(throwableMock))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.Init(TEST_SKU))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.Loading,
            ProductDetailsResult.NetworkError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchProductBySkuUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            addProductToCartUseCase
        )
    }


    @Test
    fun `when fetch variation action produce correct result`() {
        whenever(fetchProductBySkuUseCase.execute(TEST_SKU))
            .thenReturn(Single.just(dummyProductDetailsModel))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.LoadProductVariation(TEST_SKU))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.Loading,
            ProductDetailsResult.ProductData(viewModel = dummyProductDetailsModel.toProductDetailsViewModel())
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchProductBySkuUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            addProductToCartUseCase
        )
    }

    @Test
    fun `when fetch variation action error produce correct result`() {
        whenever(fetchProductBySkuUseCase.execute(TEST_SKU))
            .thenReturn(Single.error(throwableMock))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.LoadProductVariation(TEST_SKU))
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.Loading,
            ProductDetailsResult.NetworkError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(fetchProductBySkuUseCase, times(1)).execute(TEST_SKU)
        verifyZeroInteractions(
            addProductToCartUseCase
        )
    }

    @Test
    fun `when add product to cart action produce correct result`() {
        whenever(addProductToCartUseCase.execute())
            .thenReturn(Observable.just(true))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.AddProductToCart)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.CartAdditionResult(true)
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(addProductToCartUseCase, times(1)).execute()
        verifyZeroInteractions(
            fetchProductBySkuUseCase
        )
    }

    @Test
    fun `when add product to cart action error produce correct result`() {
        whenever(addProductToCartUseCase.execute())
            .thenReturn(Observable.error(throwableMock))

        val testObserver = createInteractorActionProcessor(ProductDetailsAction.AddProductToCart)
            .test()
            .addTo(disposables)

        val expectedResults = listOf(
            ProductDetailsResult.DataError
        )

        testObserver.run {
            assertValueSet(expectedResults)
            assertNoErrors()
        }

        verify(addProductToCartUseCase, times(1)).execute()
        verifyZeroInteractions(
            fetchProductBySkuUseCase
        )
    }

    // region Helpers

    private fun createInteractor(): ProductDetailsInteractor =
        ProductDetailsInteractor(
            scheduler,
            fetchProductBySkuUseCase,
            addProductToCartUseCase
        )

    private fun createInteractorActionProcessor(action: ProductDetailsAction): Observable<ProductDetailsResult> =
        Observable.fromCallable { action }.compose(interactor.actionProcessor())

    // endregion
}
