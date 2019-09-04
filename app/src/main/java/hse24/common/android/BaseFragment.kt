package hse24.common.android

import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment(), CustomBackButton {

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            onScopeFinished()
            return
        }

        if (isStateSaved) {
            return
        }

        var anyParentIsRemoving = false

        var parent = parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }

        if (isRemoving || anyParentIsRemoving) {
            onScopeFinished()
        }
    }

    @CallSuper
    protected open fun onScopeFinished() {
    }

    override fun onBackPressed(): Boolean = false

    fun addFragment(fragment: Fragment, @IdRes rootId: Int, addToBackStack: Boolean = true) {
        if (activity != null) {
            (activity as BaseActivity).addFragment(fragment, rootId, addToBackStack)
        }
    }

    fun replaceFragment(fragment: Fragment, @IdRes rootId: Int, addToBackStack: Boolean = true) {
        if (activity != null) {
            (activity as BaseActivity).replaceFragment(fragment, rootId, addToBackStack)
        }
    }
}
