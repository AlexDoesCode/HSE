package hse24.network.di

import dagger.Module
import dagger.Provides
import hse24.di.ShoppingOkHttp
import hse24.di.ShoppingRetrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(
    includes = [
        OkHttpClientModule::class
    ]
)
class NetworkModule {

    @ShoppingRetrofit
    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        @ShoppingOkHttp okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        provideRxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(provideRxJava2CallAdapterFactory)
            .addConverterFactory(gsonConverterFactory)

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

}

@Module
class OkHttpClientModule {

    private companion object {
        const val NETWORK_TIMEOUT = 10L
    }

    @ShoppingOkHttp
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
        return builder
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor {
            Timber.d(it)
        }
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
}
