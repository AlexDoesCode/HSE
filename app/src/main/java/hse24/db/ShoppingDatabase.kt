package hse24.db

import androidx.room.Database
import androidx.room.RoomDatabase
import hse24.db.dao.CartDao
import hse24.db.dao.CatalogDao
import hse24.db.dao.CategoriesDao
import hse24.db.entity.CartEntity
import hse24.db.entity.CatalogEntity
import hse24.db.entity.CategoryEntity

@Database(
    entities = [
        CategoryEntity::class,
        CatalogEntity::class,
        CartEntity::class
    ],
    exportSchema = false,
    version = ShoppingDatabase.DB_VERSION
)
abstract class ShoppingDatabase : RoomDatabase() {

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "hse24_shopping_database"
    }

    abstract fun categoriesDao(): CategoriesDao
    abstract fun catalogDao(): CatalogDao
    abstract fun cartDao(): CartDao
}
