package hse24.shop.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import hse24.shop.ShoppingActivity
import hse24.shop.cart.CartFragment
import hse24.shop.cart.di.CartScope
import hse24.shop.catalog.CatalogFragment
import hse24.shop.catalog.di.CatalogScope
import hse24.shop.categories.CategoriesFragment
import hse24.shop.categories.di.CategoriesScope
import hse24.shop.details.ProductDetailsFragment
import hse24.shop.details.di.ProductDetailsScope
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ShoppingScope

@ShoppingScope
@Subcomponent(
    modules = [
        ApiModule::class,
        FragmentModule::class
    ]
)
interface ShoppingSubcomponent : AndroidInjector<ShoppingActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ShoppingActivity>()
}

@Module(
    subcomponents = [
        ShoppingSubcomponent::class
    ]
)
abstract class ShoppingActivityModule {

    @Binds
    @IntoMap
    @ClassKey(ShoppingActivity::class)
    internal abstract fun bindShoppingActivityInjectorFactory(builder: ShoppingSubcomponent.Builder): AndroidInjector.Factory<*>
}

@Module
abstract class FragmentModule {

    @CartScope
    @ContributesAndroidInjector
    internal abstract fun contributeCartFragment(): CartFragment

    @CatalogScope
    @ContributesAndroidInjector
    internal abstract fun contributeCatalogFragment(): CatalogFragment

    @CategoriesScope
    @ContributesAndroidInjector
    internal abstract fun contributeCategoriesFragment(): CategoriesFragment

    @ProductDetailsScope
    @ContributesAndroidInjector
    internal abstract fun contributeProductDetailsFragment(): ProductDetailsFragment

}
