package hse24.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hse24.challenge.R
import hse24.common.android.BaseActivity

class ShoppingActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = Intent(context, ShoppingActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

        }
    }
}
