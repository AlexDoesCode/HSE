package hse24.common.mvi

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

interface MviIntention
interface MviInitIntention

interface MviAction
interface MviResult
interface MviState

interface MviInteractor<in Action : MviAction, out Result : MviResult> {

    fun actionProcessor(): ObservableTransformer<in Action, out Result>
}

interface MviPresenter<in Intention, State> {

    fun processIntentions(intentions: Observable<out Intention>): Disposable
    fun states(): Observable<State>
    fun destroy()
}

abstract class MviBasePresenter<Intention : MviIntention, Action : MviAction, Result : MviResult, State : MviState>(
    private val interactor: MviInteractor<Action, Result>
) : MviPresenter<Intention, State> {

    abstract val defaultState: State
    abstract val reducer: BiFunction<State, Result, State>
    abstract fun actionFromIntention(intent: Intention): Action

    private val intentionsSubject: PublishSubject<Intention> = PublishSubject.create()
    private val statesSubject: PublishSubject<State> = PublishSubject.create()
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.addAll(
            compose().subscribeWith(object : DisposableObserver<State>() {
                override fun onComplete() {
                    statesSubject.onComplete()
                }

                override fun onNext(state: State) {
                    statesSubject.onNext(state)
                }

                override fun onError(error: Throwable) {
                    statesSubject.onError(error)
                }
            })
        )
    }

    override fun states(): Observable<State> = statesSubject

    override fun processIntentions(intentions: Observable<out Intention>): Disposable {
        return intentions
            .subscribeWith(object : DisposableObserver<Intention>() {
                override fun onComplete() {
                    intentionsSubject.onComplete()
                }

                override fun onNext(intention: Intention) {
                    intentionsSubject.onNext(intention)
                }

                override fun onError(error: Throwable) {
                    intentionsSubject.onError(error)
                }

            })
    }

    override fun destroy() {
        disposable.dispose()
    }


    private fun compose(): Observable<State> {
        return intentionsSubject
            .subscribeOn(Schedulers.io())
            .compose(initIntentionFilter)
            .map(::actionFromIntention)
            .compose(interactor.actionProcessor())
            .scan(defaultState, reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private val initIntentionFilter
        get() = ObservableTransformer<Intention, Intention> { shared ->
            Observable.merge(
                listOf(
                    shared.filter { it is MviInitIntention }.take(1),
                    shared.filter { it !is MviInitIntention }
                )
            )
        }
}
