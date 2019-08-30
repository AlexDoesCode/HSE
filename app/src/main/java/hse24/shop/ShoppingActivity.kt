package hse24.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hse24.challenge.R
import hse24.common.android.BaseActivity
import hse24.shop.catalog.CatalogFragment

class ShoppingActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = Intent(context, ShoppingActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(
                CatalogFragment.newInstance(),
                R.id.shopping_activity_root,
                false
            )
        }
    }
}
