package hse24.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hse24.challenge.R
import hse24.common.android.BaseActivity
import hse24.shop.categories.CategoriesFragment

class ShoppingActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = Intent(context, ShoppingActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_activity)

        if (savedInstanceState == null) {
            replaceFragment(
                CategoriesFragment.newInstance(),
                R.id.shopping_activity_root,
                false
            )
        }
    }
}
