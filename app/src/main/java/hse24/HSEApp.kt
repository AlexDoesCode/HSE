package hse24

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import hse24.di.DaggerAppComponent

class HSEApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        val config = AppConfig(
            baseApiUrl = ""
        )

        return DaggerAppComponent.builder()
            .application(this)
            .appConfig(config)
            .build()
    }
}
