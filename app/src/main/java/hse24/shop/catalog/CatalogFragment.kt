package hse24.shop.catalog

import dagger.android.support.DaggerFragment

class CatalogFragment : DaggerFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = CatalogFragment()
    }

}
