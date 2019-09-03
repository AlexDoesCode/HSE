package hse24.common.android.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hse24.challenge.R
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.visible
import hse24.common.util.DeviceUtils

@SuppressLint("ViewConstructor")
class GalleryPagerItemLayout @JvmOverloads constructor(
    context: Context,
    private val imageUrl: String,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val image by lazy {
        findViewById<ImageView>(R.id.gallery_pager_item_image)
    }

    private val progress by lazy {
        findViewById<View>(R.id.gallery_pager_item_progress)
    }

    init {
        selfInflate(R.layout.gallery_pager_item_layout)
        doInRuntime {
            applyLayoutParams()

            Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions().apply {
                    this.fitCenter()
                })
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ) = true

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            val layoutParams = image.layoutParams
                            layoutParams.height =
                                (DeviceUtils.getScreenWidth(context).toLong() * it.intrinsicHeight / it.intrinsicWidth).toInt()
                            image.layoutParams = layoutParams
                            progress.visible = false
                        }
                        return false
                    }
                })
                .into(image)
        }
    }
}
