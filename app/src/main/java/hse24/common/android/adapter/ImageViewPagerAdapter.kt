package hse24.common.android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class ImageViewPagerAdapter(
    private val context: Context,
    private val imageUrls: List<String>
) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any) = `object` == view

    override fun getCount() = imageUrls.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = GalleryPagerItemLayout(context, imageUrls[position])
        (container as ViewPager).addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}
