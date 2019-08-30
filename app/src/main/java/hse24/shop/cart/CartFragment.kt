package hse24.shop.cart

import dagger.android.support.DaggerFragment

class CartFragment : DaggerFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = CartFragment()
    }

}
