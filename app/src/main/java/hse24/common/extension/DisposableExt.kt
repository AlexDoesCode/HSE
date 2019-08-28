package hse24.common.extension

import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

fun <T : Disposable> T.addTo(container: DisposableContainer): T = apply {
    container.add(this)
}
