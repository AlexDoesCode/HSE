package hse24.shop.cart.adapter

import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import com.hse24.challenge.R
import hse24.common.android.adapter.*
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.selfInflate
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CartAdapter(
    private val cartClickListener: CartItemClickListener
) : DelegatedAdapter() {

    private companion object {
        const val TYPE_ITEM_PRODUCT = 0
        const val TYPE_ITEM_SUM = 1
        const val TYPE_ITEM_NO_ITEMS = 2
    }

    init {
        addDelegate(TYPE_ITEM_PRODUCT, TypedAdapterDelegate { parent ->
            val layout = CartProductLayout(parent.context, cartClickListener)
            ViewHolderRenderer(layout)
        })
        addDelegate(TYPE_ITEM_SUM, TypedAdapterDelegate { parent ->
            val layout = CartSumLayout(parent.context)
            ViewHolderRenderer(layout)
        })
        addDelegate(TYPE_ITEM_NO_ITEMS, TypedAdapterDelegate { parent ->
            val layout = object : ItemRenderer<CartItemViewModel>,
                FrameLayout(parent.context, null, android.R.attr.progressBarStyleLarge) {
                override fun render(data: CartItemViewModel) {
                    // no-op
                }
            }.apply {
                selfInflate(R.layout.cart_empty_layout)
                applyLayoutParams()
            }
            ViewHolderRenderer(layout)
        })
    }

    override var items: List<CartItemViewModel> = emptyList()

    override fun getItemViewType(position: Int) = when (items[position]) {
        is CartItemViewModel.ProductItem -> TYPE_ITEM_PRODUCT
        is CartItemViewModel.OverallSum -> TYPE_ITEM_SUM
        is CartItemViewModel.EmptyCart -> TYPE_ITEM_NO_ITEMS
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<CartItemViewModel>): Completable {
        return Single.fromCallable<DiffUtil.DiffResult> {
            DiffUtil.calculateDiff(CommonDiffUtilCallback(items, newItems))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { result ->
                this.items = newItems
                result.dispatchUpdatesTo(this)
            }
            .ignoreElement()
    }

    interface CartItemClickListener {

        fun onItemClick(sku: Int)
        fun onRemoveItem(sku: Int)
    }
}
