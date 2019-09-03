package hse24.shop.cart

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hse24.challenge.R
import hse24.common.android.BaseFragment
import hse24.shop.cart.adapter.CartAdapter
import hse24.shop.cart.mvi.CartIntention
import hse24.shop.cart.mvi.CartPresenter
import hse24.shop.cart.mvi.CartState
import hse24.shop.details.ProductDetailsFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CartFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = CartFragment()
    }

    private val intentionsSubject = PublishSubject.create<CartIntention>()

    private val cartClickListener = object : CartAdapter.CartItemClickListener {

        override fun onItemClick(sku: Int) {
            replaceFragment(
                ProductDetailsFragment.newInstance(sku),
                R.id.shopping_activity_root,
                false
            )
        }

        override fun onRemoveItem(sku: Int) {
            intentionsSubject.onNext(CartIntention.RemoveItem(sku))
        }
    }

    private val cartAdapter = CartAdapter(cartClickListener)


    private var adapterDisposable: Disposable? = null

    @Inject
    lateinit var presenter: CartPresenter

    private lateinit var recycler: RecyclerView
    private lateinit var disposables: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cart_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.cart_fragment_recycler)
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

        recycler.apply {
            adapter = this@CartFragment.cartAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                RecyclerMarginDecoration(
                    resources.getDimension(R.dimen.margin_1dp).toInt()
                )
            )
        }
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
    private fun render(state: CartState) {
        Timber.d("State is $state")
        with(state) {

            error.get(this)?.let {
                if (it) {
                    Toast.makeText(
                        context,
                        getString(R.string.common_data_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            cartItems?.let {
                adapterDisposable?.dispose()
                adapterDisposable = cartAdapter.setItems(it)
                    .subscribe()
            }

        }
    }

    private fun intentions() = Observable.merge(
        listOf(
            Observable.just(CartIntention.Init),
            intentionsSubject
        )
    )

    private class RecyclerMarginDecoration(
        val margin: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = margin
                }
                bottom = margin
            }
        }
    }

}
