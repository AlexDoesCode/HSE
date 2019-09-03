package hse24.common.android

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class DynamicHeightViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec

        var height = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)

            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            val childHeight = child.measuredHeight
            if (childHeight > height) {
                height = childHeight
            }
        }

        if (height != 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}
