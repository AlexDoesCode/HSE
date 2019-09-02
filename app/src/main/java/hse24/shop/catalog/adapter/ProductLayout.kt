package hse24.shop.catalog.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.visible

@SuppressLint("ViewConstructor")
class ProductLayout @JvmOverloads constructor(
    context: Context,
    clickListener: CatalogAdapter.CatalogItemClickListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ItemRenderer<CatalogItemViewModel> {

    private lateinit var viewModel: CatalogItemViewModel.ProductViewModel

    private val productImage by lazy {
        findViewById<ImageView>(R.id.catalog_product_item_image)
    }

    private val brandName by lazy {
        findViewById<TextView>(R.id.catalog_product_item_brand_name)
    }

    private val name by lazy {
        findViewById<TextView>(R.id.catalog_product_item_name)
    }

    private val price by lazy {
        findViewById<TextView>(R.id.catalog_product_item_price)
    }

    private val imageProgress by lazy {
        findViewById<View>(R.id.catalog_product_item_progress)
    }

    init {
        selfInflate(R.layout.catalog_product_layout)
        doInRuntime {
            applyLayoutParams()
            setBackgroundColor(getColor(context, R.color.white))
            setOnClickListener {
                clickListener.onProductClick(viewModel.sku)
            }
        }
    }

    override fun render(data: CatalogItemViewModel) {
        if (data is CatalogItemViewModel.ProductViewModel) {
            with(data) {
                viewModel = this

                context?.let {
                    Glide.with(it)
                        .load(data.imageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ) = true

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageProgress.visible = false
                                return false
                            }
                        })
                        .apply(RequestOptions().apply {
                            this.fitCenter()
                        })
                        .into(productImage)
                }

                this@ProductLayout.brandName.text = brandName
                this@ProductLayout.name.text = name
                this@ProductLayout.price.text = price
            }
        }
    }
}
