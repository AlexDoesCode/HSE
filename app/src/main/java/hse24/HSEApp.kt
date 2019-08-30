package hse24

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import hse24.di.DaggerAppComponent

class HSEApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        val config = AppConfig(
            baseApiUrl = "https://www.hse24.de/ext-api/app",
            baseImageRetrievalUrl = "https://pic.hse24-dach.net/media/de/products",
            deviceTypeHeader = "ANDROID_PHONE",
            localeTypeHeader = "de_DE"
        )

        return DaggerAppComponent.builder()
            .application(this)
            .appConfig(config)
            .build()
    }
}
