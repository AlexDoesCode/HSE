package hse24.common.extension

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.selfInflate(@LayoutRes resId: Int) {
    View.inflate(context, resId, this)
}
