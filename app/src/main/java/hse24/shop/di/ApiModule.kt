package hse24.shop.di

import dagger.Module
import dagger.Provides
import hse24.AppConfig
import hse24.di.ApplicationConfig
import hse24.di.ShoppingRetrofit
import hse24.network.ImageRetreivalApi
import hse24.network.ShoppingApi
import retrofit2.Retrofit

@Module
class ApiModule {

    @Provides
    fun provideShoppingApi(
        @ShoppingRetrofit retrofitBuilder: Retrofit.Builder,
        @ApplicationConfig appConfig: AppConfig
    ): ShoppingApi =
        retrofitBuilder
            .baseUrl(appConfig.baseApiUrl)
            .build()
            .create(ShoppingApi::class.java)

    @Provides
    fun provideImageRetreivalApi(
        @ShoppingRetrofit retrofitBuilder: Retrofit.Builder,
        @ApplicationConfig appConfig: AppConfig
    ): ImageRetreivalApi =
        retrofitBuilder
            .baseUrl(appConfig.baseImageRetrievalUrl)
            .build()
            .create(ImageRetreivalApi::class.java)
}
