package hse24.shop.cart.mvi

import hse24.common.mvi.MviInteractor
import hse24.db.entity.toCartProductViewModel
import hse24.di.IoScheduler
import hse24.shop.cart.di.CartScope
import hse24.shop.repository.CartRepository
import hse24.shop.usecase.cart.RemoveCartItemUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@CartScope
class CartInteractor @Inject constructor(
    @IoScheduler private val ioScheduler: Scheduler,
    private val cartRepository: CartRepository,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : MviInteractor<CartAction, CartResult> {

    private val initProcessor: ObservableTransformer<in CartAction.Init, out CartResult> =
        ObservableTransformer { action ->
            action
                .switchMap {
                    cartRepository.observeCart()
                        .map { entities ->
                            CartResult.CartItems(entities.map { item -> item.toCartProductViewModel() }) as CartResult
                        }
                        .subscribeOn(ioScheduler)
                        .onErrorReturn { throwable ->
                            if (throwable !is IOException) {
                                Timber.e(throwable)
                            } else {
                                Timber.d(throwable)
                            }
                            CartResult.Error
                        }
                }
        }

    private val removeItemProcessor: ObservableTransformer<in CartAction.RemoveAction, out CartResult> =
        ObservableTransformer { action ->
            action
                .flatMapCompletable {
                    removeCartItemUseCase.execute(it.sku)
                }
                .toObservable()
        }

    override fun actionProcessor(): ObservableTransformer<in CartAction, out CartResult> =
        ObservableTransformer { action ->
            action
                .observeOn(ioScheduler)
                .publish { shared ->
                    Observable.merge(
                        listOf(
                            shared.ofType(CartAction.Init::class.java)
                                .compose(initProcessor),
                            shared.ofType(CartAction.RemoveAction::class.java)
                                .compose(removeItemProcessor)
                        )
                    )
                }

        }
}
