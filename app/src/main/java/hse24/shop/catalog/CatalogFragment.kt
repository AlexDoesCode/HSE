package hse24.shop.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hse24.challenge.R
import hse24.common.android.BaseFragment
import hse24.common.android.FragmentArgumentDelegate
import hse24.shop.catalog.adapter.CatalogAdapter
import hse24.shop.catalog.adapter.CatalogItemViewModel
import hse24.shop.catalog.mvi.CatalogError
import hse24.shop.catalog.mvi.CatalogIntention
import hse24.shop.catalog.mvi.CatalogPresenter
import hse24.shop.catalog.mvi.CatalogState
import hse24.shop.details.ProductDetailsFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CatalogFragment : BaseFragment() {

    companion object {

        private const val RECYCLER_ROW_ITEMS_SPAN = 1
        private const val RECYCLER_LOADING_ROW_SPAN = 2

        @JvmStatic
        fun newInstance(categoryId: Int, categoryName: String) = CatalogFragment()
            .apply {
                this.categoryId = categoryId
                this.categoryName = categoryName
            }
    }

    private val intentionsSubject = PublishSubject.create<CatalogIntention>()

    private val adapterClickListener = object : CatalogAdapter.CatalogItemClickListener {

        override fun onProductClick(id: Int) {
            Timber.d("Product $id clicked")
            replaceFragment(
                ProductDetailsFragment.newInstance(id),
                R.id.shopping_activity_root,
                false
            )
        }
    }

    private val adapterViewHolderBindListener = object : CatalogAdapter.ViewHolderBindListener {

        override fun onBinded(itemsLeft: Int) {
            if (isLastPageReached.not()) {
                intentionsSubject.onNext(CatalogIntention.LoadNextPage(categoryId))
            }
        }
    }

    private var categoryId by FragmentArgumentDelegate<Int>()
    private var categoryName by FragmentArgumentDelegate<String>()

    @Inject
    lateinit var presenter: CatalogPresenter

    private lateinit var disposables: CompositeDisposable

    private var isLastPageReached: Boolean = false
    private var adapterDisposable: Disposable? = null
    private var catalogAdapter = CatalogAdapter(adapterClickListener, adapterViewHolderBindListener)

    private lateinit var catalogRecycler: RecyclerView
    private lateinit var titleText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.catalog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        catalogRecycler = view.findViewById(R.id.catalog_fragment_products_recycler)
        titleText = view.findViewById(R.id.catalog_fragment_title)

        titleText.text = categoryName

        catalogRecycler.apply {
            adapter = this@CatalogFragment.catalogAdapter
            layoutManager = GridLayoutManager(context, 2)
                .apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int) =
                            if (catalogAdapter.items[position] is CatalogItemViewModel.LoadMore) {
                                RECYCLER_LOADING_ROW_SPAN
                            } else {
                                RECYCLER_ROW_ITEMS_SPAN
                            }
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()

        disposables = CompositeDisposable(
            presenter.states()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::render),
            presenter.processIntentions(
                intentions()
            )
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
    private fun render(state: CatalogState) {
        Timber.d("State is $state")
        with(state) {

            this@CatalogFragment.isLastPageReached = isLastPageReached

            catalogItems?.let {
                adapterDisposable?.dispose()
                adapterDisposable = catalogAdapter.setItems(it)
                    .subscribe()
            }

            error.get(this)?.let {
                Toast.makeText(
                    context,
                    when (it) {
                        CatalogError.DATA -> getString(R.string.common_data_error)
                        CatalogError.NETWORK -> getString(R.string.common_data_error)
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun intentions() = Observable.merge(
        listOf(
            Observable.just(CatalogIntention.Init(categoryId)),
            intentionsSubject
        )
    )

}
