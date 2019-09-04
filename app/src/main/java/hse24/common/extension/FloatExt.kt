package hse24.common.extension

import kotlin.math.round

fun Float.round(amount: Int): Float {
    var multiplier = 1.0
    repeat(amount) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier).toFloat()
}
