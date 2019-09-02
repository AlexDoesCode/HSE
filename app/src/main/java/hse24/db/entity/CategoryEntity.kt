package hse24.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hse24.db.entity.CategoryEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ENTITY_ID)
    val id: Int,

    @ColumnInfo(name = NAME)
    val name: String,

    @ColumnInfo(name = PARENT_ID)
    val parentId: Int
) {

    companion object {
        const val TABLE_NAME = "categories_data"
        const val ENTITY_ID = "entity_id"
        const val NAME = "name"
        const val PARENT_ID = "parent_entity_id"
        const val DEPARTMENT_PARENT_ID = 0
    }
}
