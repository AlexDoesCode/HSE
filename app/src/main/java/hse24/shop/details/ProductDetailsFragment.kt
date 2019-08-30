package hse24.shop.details

import dagger.android.support.DaggerFragment

class ProductDetailsFragment : DaggerFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = ProductDetailsFragment()
    }
}
