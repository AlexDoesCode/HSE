package hse24.common.extension

fun String.toCurrency() = when (this) {
    "EUR" -> "\u20ac"
    else -> this
}
