package hse24.shop.cart.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.toCurrency

@SuppressLint("ViewConstructor")
class CartSumLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ItemRenderer<CartItemViewModel> {

    init {
        selfInflate(R.layout.cart_sum_layout)
        doInRuntime {
            applyLayoutParams()
            setBackgroundColor(ContextCompat.getColor(context, R.color.lighter_grey))
        }
    }

    private val price by lazy {
        findViewById<TextView>(R.id.cart_sum_price)
    }

    override fun render(data: CartItemViewModel) {
        if (data is CartItemViewModel.OverallSum) {
            price.text = "${data.currency.toCurrency()} ${data.sum}"
        }
    }
}
