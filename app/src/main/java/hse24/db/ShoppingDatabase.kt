package hse24.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
//        CartEntity::class,
//        GoodsEntity::class
    ],
    exportSchema = true,
    version = ShoppingDatabase.DB_VERSION
)
abstract class ShoppingDatabase : RoomDatabase() {

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "hse24_shopping_database"
    }

//    abstract fun goodsDao(): GoodsDao
//    abstract fun cartDao(): CartDao
}
