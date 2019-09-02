package hse24.common.extension

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes

inline fun View.doInRuntime(code: () -> Unit) {
    if (!isInEditMode) code()
}

fun View.applyLayoutParams(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    layoutParams = this.layoutParams?.apply {
        this.width = width
        this.height = height
    } ?: ViewGroup.MarginLayoutParams(width, height)
}

var View.visible
    get() = isVisible()
    set(value) = if (value) setVisible() else setGone()

fun View.isVisible() = visibility == View.VISIBLE
fun View.setVisible() {
    if (!isVisible())
        visibility = View.VISIBLE
}

fun View.isGone() = visibility == View.GONE
fun View.setGone() {
    if (!isGone())
        visibility = View.GONE
}

fun View.setMarginTopResCompat(@DimenRes marginRes: Int) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.topMargin = context.getDimensionPixelSizeCompat(marginRes)
    layoutParams = params
}
