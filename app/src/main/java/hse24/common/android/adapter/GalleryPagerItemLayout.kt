package hse24.common.android.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.hse24.challenge.R
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.visible

@SuppressLint("ViewConstructor")
class GalleryPagerItemLayout @JvmOverloads constructor(
    context: Context,
    private val imageUrl: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val image by lazy {
        findViewById<ImageView>(R.id.categories_category_collapse_up)
    }

    private val progress by lazy {
        findViewById<ImageView>(R.id.categories_category_collapse_down)
    }

    init {
        selfInflate(R.layout.gallery_pager_item_layout)
        doInRuntime {
            applyLayoutParams()

            Glide.with(context)
                .load(imageUrl)
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
                        progress.visible = false
                        return false
                    }
                })
                .apply(RequestOptions().apply {
                    this.fitCenter()
                })
                .into(image)
        }
    }
}
