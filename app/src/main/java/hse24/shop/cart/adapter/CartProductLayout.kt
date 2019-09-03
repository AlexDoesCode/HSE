package hse24.shop.cart.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.toCurrency


@SuppressLint("ViewConstructor")
class CartProductLayout @JvmOverloads constructor(
    context: Context,
    private val clickListener: CartAdapter.CartItemClickListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ItemRenderer<CartItemViewModel> {

    private val productName by lazy {
        findViewById<TextView>(R.id.cart_item_name)
    }

    private val productBrand by lazy {
        findViewById<TextView>(R.id.cart_item_brand)
    }

    private val productImage by lazy {
        findViewById<ImageView>(R.id.cart_item_image)
    }

    private val productVariation by lazy {
        findViewById<TextView>(R.id.cart_item_variation)
    }

    private val productPrice by lazy {
        findViewById<TextView>(R.id.cart_item_price)
    }

    private val removeButton by lazy {
        findViewById<View>(R.id.cart_item_remove)
    }

    init {
        selfInflate(R.layout.cart_item_layout)
        doInRuntime {
            applyLayoutParams()
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            removeButton.setOnClickListener {
                clickListener.onRemoveItem(viewModel.sku)
            }

            setOnClickListener {
                clickListener.onItemClick(viewModel.sku)
            }
        }
    }

    private lateinit var viewModel: CartItemViewModel.ProductItem

    override fun render(data: CartItemViewModel) {
        if (data is CartItemViewModel.ProductItem) {
            viewModel = data

            with(data) {
                productName.text = name
                productBrand.text = brandName
                productPrice.text = "${currency.toCurrency()} $price}"
                productVariation.text = variation

                Glide.with(context)
                    .load(imageUrl)
                    .apply(RequestOptions().apply {
                        this.fitCenter()
                    })
                    .into(productImage)
            }
        }
    }
}
