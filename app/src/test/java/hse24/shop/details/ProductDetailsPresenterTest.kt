package hse24.shop.details

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import hse24.common.extension.addTo
import hse24.common.mvi.OneShot
import hse24.db.entity.toProductDetailsViewModel
import hse24.shop.details.mvi.*
import hse24.shop.repository.ProductDetailsModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductDetailsPresenterTest {

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

    private lateinit var disposables: CompositeDisposable
    private lateinit var presenter: ProductDetailsPresenter

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
        presenter = createPresenter { ProductDetailsResult.NetworkError }

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
            Observable.just(ProductDetailsIntention.Init(TEST_SKU))
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
            Observable.just(ProductDetailsIntention.Init(TEST_SKU))
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    @Test
    fun `when load variation intention produces correct state`() {
        val (result, state) = createLoadVariationPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
//                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(ProductDetailsIntention.LoadProductVariation(TEST_SKU))
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    @Test
    fun `when load variation intention produces error`() {
        val (result, state) = createLoadVariationErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(ProductDetailsIntention.LoadProductVariation(TEST_SKU))
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    @Test
    fun `when add to cart intention produces correct state`() {
        val (result, state) = createAddToCartPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(ProductDetailsIntention.AddProductToCart)
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    @Test
    fun `when add to cart intention produces error`() {
        val (result, state) = createAddToCartErrorPair()
        presenter = createPresenter { result }
        val testObserver =
            presenter
                .states()
                .test()
                .addTo(disposables)

        presenter.processIntentions(
            Observable.just(ProductDetailsIntention.AddProductToCart)
        )
            .addTo(disposables)

        testObserver.assertValue(state)
    }

    //endregion

    // region Helpers

    private fun createPresenter(actionMapper: (ProductDetailsAction) -> ProductDetailsResult): ProductDetailsPresenter {
        val interactor = mock<ProductDetailsInteractor> {
            on { actionProcessor() } doReturn ObservableTransformer {
                it.map { actionMapper(it) }
            }
        }
        return ProductDetailsPresenter(interactor)
    }

    private fun createInitialPair(): Pair<ProductDetailsResult.ProductData, ProductDetailsState> {
        val result = ProductDetailsResult.ProductData(viewModel = dummyProductDetailsModel.toProductDetailsViewModel())
        val state = ProductDetailsState(productData = dummyProductDetailsModel.toProductDetailsViewModel())
        return result to state
    }

    private fun createInitialErrorPair(): Pair<ProductDetailsResult.NetworkError, ProductDetailsState> {
        val result = ProductDetailsResult.NetworkError
        val state = ProductDetailsState(error = OneShot(ProductDetailsError.NETWORK))
        return result to state
    }

    private fun createLoadVariationPair(): Pair<ProductDetailsResult.ProductData, ProductDetailsState> {
        val result = ProductDetailsResult.ProductData(viewModel = dummyProductDetailsModel.toProductDetailsViewModel())
        val state = ProductDetailsState(productData = dummyProductDetailsModel.toProductDetailsViewModel())
        return result to state
    }

    private fun createLoadVariationErrorPair(): Pair<ProductDetailsResult.NetworkError, ProductDetailsState> {
        val result = ProductDetailsResult.NetworkError
        val state = ProductDetailsState(error = OneShot(ProductDetailsError.NETWORK))
        return result to state
    }

    private fun createAddToCartPair(): Pair<ProductDetailsResult.CartAdditionResult, ProductDetailsState> {
        val result = ProductDetailsResult.CartAdditionResult(true)
        val state = ProductDetailsState(addToCartState = OneShot(true))
        return result to state
    }

    private fun createAddToCartErrorPair(): Pair<ProductDetailsResult.DataError, ProductDetailsState> {
        val result = ProductDetailsResult.DataError
        val state = ProductDetailsState(error = OneShot(ProductDetailsError.DATA))
        return result to state
    }

    //endregion

}
