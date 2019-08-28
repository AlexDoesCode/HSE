package hse24.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppEnvironment

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoScheduler

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ShoppingOkHttp

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ShoppingRetrofit
