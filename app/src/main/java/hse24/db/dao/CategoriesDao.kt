package hse24.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import hse24.db.entity.CategoryEntity
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

@Dao
abstract class CategoriesDao {

    @Insert
    abstract fun insert(entity: CategoryEntity)

    @Query("DELETE FROM ${CategoryEntity.TABLE_NAME}")
    abstract fun clearAll()

    @Query(
        """
            SELECT
            *
            FROM
            ${CategoryEntity.TABLE_NAME}
            WHERE
            ${CategoryEntity.PARENT_ID} = (${CategoryEntity.DEPARTMENT_PARENT_ID})
        """
    )
    abstract fun observeDepartments(): Flowable<List<CategoryEntity>>

    @Query(
        """
            SELECT
            *
            FROM
            ${CategoryEntity.TABLE_NAME}
            WHERE
            ${CategoryEntity.PARENT_ID} = :parentCategoryId
        """
    )
    abstract fun getCategoriesByParentId(parentCategoryId: Int): Maybe<List<CategoryEntity>>

    @Query(
        """
            SELECT
            *
            FROM
            ${CategoryEntity.TABLE_NAME} AS CATEGORY
            WHERE
            ${CategoryEntity.PARENT_ID} = :departmentId
            AND EXISTS (
                SELECT
                *
                FROM
                ${CategoryEntity.TABLE_NAME}
                WHERE
                ${CategoryEntity.PARENT_ID} = CATEGORY.entity_id
            )
        """
    )
    abstract fun getCategoriesWithChildren(departmentId: Int): Maybe<List<CategoryEntity>>

    @Query(
        """
            SELECT
            *
            FROM
            ${CategoryEntity.TABLE_NAME} AS CATEGORY
            WHERE
            ${CategoryEntity.PARENT_ID} = :departmentId
            AND NOT EXISTS (
                SELECT
                *
                FROM
                ${CategoryEntity.TABLE_NAME}
                WHERE
                ${CategoryEntity.PARENT_ID} = CATEGORY.entity_id
            )
        """
    )
    abstract fun getCategoriesWithoutChildren(departmentId: Int): Maybe<List<CategoryEntity>>

    @Transaction
    open fun insertAll(categories: List<CategoryEntity>) {
        clearAll()

        Timber.d("Inserting ${categories.size} categories")

        categories.forEach {
            Timber.d("$it")
        }

        categories.forEach {
            insert(it)
        }
    }
}
