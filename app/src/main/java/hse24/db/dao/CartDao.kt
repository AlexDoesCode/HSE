package hse24.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hse24.db.entity.CartEntity
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: CartEntity): Long

    @Query(
        """
            SELECT
            *
            FROM
            ${CartEntity.TABLE_NAME}
        """
    )
    abstract fun observeCart(): Flowable<List<CartEntity>>

    @Query(
        """
            SELECT
            COUNT(*)
            FROM
            ${CartEntity.TABLE_NAME}
        """
    )
    abstract fun getEntitiesCount(): Maybe<Int>

    @Query(
        """
            DELETE FROM
            ${CartEntity.TABLE_NAME}
            WHERE
            ${CartEntity.SKU} = :sku
            
        """
    )
    abstract fun removeEntity(sku: Int)

    @Query(
        """
            SELECT
            *
            FROM
            ${CartEntity.TABLE_NAME}
            WHERE
            ${CartEntity.SKU} = :sku
            
        """
    )
    abstract fun getProductsFromCartBySku(sku: Int): Maybe<List<CartEntity>>

    @Query("DELETE FROM ${CartEntity.TABLE_NAME}")
    abstract fun clearAll()
}
