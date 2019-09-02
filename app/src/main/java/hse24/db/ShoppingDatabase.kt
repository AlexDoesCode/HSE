package hse24.db

import androidx.room.Database
import androidx.room.RoomDatabase
import hse24.db.dao.CategoriesDao
import hse24.db.entity.CategoryEntity

@Database(
    entities = [
        CategoryEntity::class
//        CartEntity::class,
//        GoodsEntity::class
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
//    abstract fun goodsDao(): GoodsDao
//    abstract fun cartDao(): CartDao
}
