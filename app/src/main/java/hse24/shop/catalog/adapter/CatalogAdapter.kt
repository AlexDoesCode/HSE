package hse24.shop.catalog.adapter

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hse24.challenge.R
import hse24.common.android.adapter.*
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.getDimensionPixelSizeCompat
import hse24.common.extension.setMarginTopResCompat
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CatalogAdapter(
    private val clickListener: CatalogItemClickListener,
    private val viewHolderBindListener: ViewHolderBindListener
) : DelegatedAdapter() {

    private companion object {
        const val TYPE_ITEM_PRODUCT = 0
        const val TYPE_ITEM_LOAD_MORE = 1
        const val ITEMS_LIMIT = 6
    }

    init {
        addDelegate(TYPE_ITEM_PRODUCT, TypedAdapterDelegate { parent ->
            val layout = ProductLayout(parent.context, clickListener)
            ViewHolderRenderer(layout)
        })
        addDelegate(TYPE_ITEM_LOAD_MORE, TypedAdapterDelegate { parent ->
            val layout = object : ItemRenderer<CatalogItemViewModel>,
                ProgressBar(parent.context, null, android.R.attr.progressBarStyleLarge) {
                override fun render(data: CatalogItemViewModel) {
                    // no-op
                }
            }.apply {
                applyLayoutParams(MATCH_PARENT, parent.context.getDimensionPixelSizeCompat(R.dimen.height_50dp))
                setMarginTopResCompat(R.dimen.margin_15dp)
                indeterminateDrawable.setTint(ContextCompat.getColor(context, R.color.hse_primary))
            }
            ViewHolderRenderer(layout)
        })
    }

    override var items: List<CatalogItemViewModel> = emptyList()

    override fun getItemViewType(position: Int) = when (items[position]) {
        is CatalogItemViewModel.ProductViewModel -> TYPE_ITEM_PRODUCT
        is CatalogItemViewModel.LoadMore -> TYPE_ITEM_LOAD_MORE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val itemsLeft = items.size - position
        if (itemsLeft < ITEMS_LIMIT)
            viewHolderBindListener.onBinded(itemsLeft)
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<CatalogItemViewModel>) =
        Single.fromCallable<DiffUtil.DiffResult> {
            DiffUtil.calculateDiff(CommonDiffUtilCallback(items, newItems))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { result ->
                this.items = newItems
                result.dispatchUpdatesTo(this)
            }
            .ignoreElement()

    interface CatalogItemClickListener {

        fun onProductClick(id: Int)
    }

    interface ViewHolderBindListener {
        fun onBinded(itemsLeft: Int)
    }
}
