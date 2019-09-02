package hse24.db.dao

import androidx.room.*
import hse24.db.entity.CatalogEntity
import io.reactivex.Flowable

@Dao
abstract class CatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: CatalogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(entity: List<CatalogEntity>): List<Long>

    @Query("DELETE FROM ${CatalogEntity.TABLE_NAME}")
    abstract fun clearAll()

    @Query(
        """
            SELECT
            *
            FROM
            ${CatalogEntity.TABLE_NAME}
            WHERE
            ${CatalogEntity.CATEGORY_ID_FK} = :categoryId
        """
    )
    abstract fun observeProductsByCategoryId(categoryId: Int): Flowable<List<CatalogEntity>>

    @Transaction
    open fun insertFromFirstPage(productsEntities: List<CatalogEntity>) {
        clearAll()
        productsEntities.forEach {
            insert(it)
        }
    }
}
