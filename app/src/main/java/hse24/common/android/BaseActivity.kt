package hse24.common.android

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

    fun addFragment(fragment: Fragment, @IdRes rootId: Int, addToBackStack: Boolean = true) {
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .add(rootId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    fun replaceFragment(fragment: Fragment, @IdRes rootId: Int, addToBackStack: Boolean = true) {
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .replace(rootId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }
}
