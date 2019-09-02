package hse24.common.extension

import android.content.Context
import androidx.annotation.DimenRes

fun Context.getDimensionPixelSizeCompat(@DimenRes resId: Int): Int = resources.getDimensionPixelSize(resId)
