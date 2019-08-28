package hse24.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import hse24.AppConfig
import hse24.HSEApp
import hse24.db.di.DatabaseModule
import hse24.network.di.NetworkModule
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface AppComponent : AndroidInjector<HSEApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: HSEApp): Builder

        @BindsInstance
        fun appConfig(applicationConfig: AppConfig): Builder

        fun build(): AppComponent
    }

    override fun inject(application: HSEApp)
}

@Module
class AppModule {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    @IoScheduler
    fun provideIoScheduler(): Scheduler =
        Schedulers.io()

    @Provides
    @Singleton
    @AppEnvironment
    fun provideEnvironmentConfig(applicationConfig: AppConfig) = applicationConfig
}

//@Module(
//    includes = [
//        ShoppingActivityModule::class
//    ]
//)
//abstract class ActivityModule {
//
//    @ContributesAndroidInjector
//    internal abstract fun contributeMainActivity(): MainActivity
//}
