package hse24.db.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import hse24.db.ShoppingDatabase
import hse24.di.ApplicationContext
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideNewsDatabase(
        @ApplicationContext
        context: Context
    ): ShoppingDatabase =
        Room
            .databaseBuilder(
                context,
                ShoppingDatabase::class.java,
                ShoppingDatabase.DB_NAME
            )
            .build()
}
