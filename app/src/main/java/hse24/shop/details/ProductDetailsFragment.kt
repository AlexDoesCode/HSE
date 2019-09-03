package hse24.shop.details

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.viewpager.widget.ViewPager
import com.hse24.challenge.R
import com.jakewharton.rxbinding2.view.RxView
import hse24.common.android.BaseFragment
import hse24.common.android.FragmentArgumentDelegate
import hse24.common.android.adapter.ImageViewPagerAdapter
import hse24.common.extension.visible
import hse24.shop.details.model.ProductVariationViewModel
import hse24.shop.details.mvi.ProductDetailsError
import hse24.shop.details.mvi.ProductDetailsIntention
import hse24.shop.details.mvi.ProductDetailsPresenter
import hse24.shop.details.mvi.ProductDetailsState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.relex.circleindicator.CircleIndicator
import timber.log.Timber
import javax.inject.Inject

class ProductDetailsFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(productSku: Int) = ProductDetailsFragment()
            .apply {
                this.productSku = productSku
            }
    }

    private val intentionsSubject = PublishSubject.create<ProductDetailsIntention>()

    private val variantsDialogClickListener = DialogInterface.OnClickListener { _, variationPosition ->
        intentionsSubject.onNext(
            ProductDetailsIntention.LoadProductVariation(variations[variationPosition].sku)
        )
    }

    private var productSku by FragmentArgumentDelegate<Int>()

    private var adapter: ImageViewPagerAdapter? = null

    @Inject
    lateinit var presenter: ProductDetailsPresenter
    lateinit var variations: List<ProductVariationViewModel>

    private lateinit var title: TextView
    private lateinit var name: TextView
    private lateinit var brandName: TextView
    private lateinit var price: TextView
    private lateinit var variationsView: TextView
    private lateinit var description: TextView

    private lateinit var progress: View
    private lateinit var addToCartButton: View

    private lateinit var viewPager: ViewPager
    private lateinit var circleIndicator: CircleIndicator

    private lateinit var disposables: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.product_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.product_details_fragment_title)
        name = view.findViewById(R.id.product_details_fragment_name)
        brandName = view.findViewById(R.id.product_details_fragment_brand)
        price = view.findViewById(R.id.product_details_fragment_price)
        variationsView = view.findViewById(R.id.product_details_fragment_variants)
        description = view.findViewById(R.id.product_details_fragment_description)
        progress = view.findViewById(R.id.product_details_fragment_progress)
        addToCartButton = view.findViewById(R.id.product_details_fragment_add_to_cart)

        viewPager = view.findViewById(R.id.product_details_fragment_view_pager)
        circleIndicator = view.findViewById(R.id.product_details_fragment_circle_indicator)
    }

    override fun onStart() {
        super.onStart()

        disposables = CompositeDisposable(
            presenter.states()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::render),
            presenter.processIntentions(
                intentions()
            ),
            RxView.clicks(addToCartButton)
                .skip(1)
                .subscribe {
                    intentionsSubject.onNext(ProductDetailsIntention.AddProductToCart)
                },
            RxView.clicks(variationsView)
                .skip(1)
                .subscribe {
                    context?.let {
                        AlertDialog.Builder(it)
                            .apply {
                                title.text = getString(R.string.product_details_fragment_choose_variant)
                                setItems(
                                    variations.map { it.name }.toTypedArray(),
                                    variantsDialogClickListener
                                )
                            }
                            .create()
                    }
                }
        )
    }

    override fun onStop() {
        disposables.dispose()
        super.onStop()
    }

    override fun onScopeFinished() {
        presenter.destroy()
        super.onScopeFinished()
    }

    @UiThread
    private fun render(state: ProductDetailsState) {
        Timber.d("State is $state")
        with(state) {
            progress.visible = isLoading

            error.get(this)?.let {
                Toast.makeText(
                    context,
                    when (it) {
                        ProductDetailsError.DATA -> getString(R.string.common_data_error)
                        ProductDetailsError.NETWORK -> getString(R.string.common_data_error)
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }

            addToCartState.get(this)?.let {
                Toast.makeText(
                    context,
                    when (it) {
                        true -> getString(R.string.product_details_fragment_added_to_cart)
                        false -> getString(R.string.product_details_fragment_already_in_cart)
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }

            productData?.let {
                title.text = it.title
                name.text = it.name
                brandName.text = it.brandName
                price.text = it.price
                variationsView.text = it.currentVariation
                description.text = it.description
                variations = it.variations

                variationsView.visible = it.variations.isNullOrEmpty().not()

                context?.let { ctxt ->
                    if (adapter == null) {
                        adapter = ImageViewPagerAdapter(
                            ctxt,
                            it.imageUrls
                        )

                        viewPager.adapter = adapter
                        circleIndicator.setViewPager(viewPager)
                    }
                }
            }
        }
    }

    private fun intentions() = Observable.merge(
        listOf(
            Observable.just(ProductDetailsIntention.Init(productSku)),
            intentionsSubject
        )
    )
}
