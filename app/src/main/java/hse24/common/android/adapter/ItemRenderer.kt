package hse24.common.android.adapter

interface ItemRenderer<in DATA> {
    fun render(data: DATA)
}
