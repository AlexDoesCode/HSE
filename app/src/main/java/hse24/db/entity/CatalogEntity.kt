package hse24.db.entity

import androidx.room.*
import hse24.db.entity.CatalogEntity.Companion.CATEGORY_ID_FK
import hse24.db.entity.CatalogEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [CATEGORY_ID_FK])],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = [CategoryEntity.ENTITY_ID],
            childColumns = [CATEGORY_ID_FK],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CatalogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ENTITY_ID)
    val id: Int,

    @ColumnInfo(name = ENTITY_SKU)
    val sku: Int,

    @ColumnInfo(name = NAME)
    val productName: String,

    @ColumnInfo(name = BRAND_NAME)
    val brandName: String,

    @ColumnInfo(name = PRICE)
    val price: Float,

    @ColumnInfo(name = CURRENCY)
    val currencySymbol: String,

    @ColumnInfo(name = IMAGE_URL)
    val imageUrl: String,

    @ColumnInfo(name = CATEGORY_ID_FK)
    val categoryIdFk: Int
) {

    companion object {
        const val TABLE_NAME = "catalog_products"
        const val ENTITY_ID = "entity_id"
        const val ENTITY_SKU = "entity_sku"
        const val NAME = "product_name"
        const val BRAND_NAME = "brand_name"
        const val PRICE = "product_price"
        const val CURRENCY = "price_currency"
        const val IMAGE_URL = "image_uri"
        const val CATEGORY_ID_FK = "category_id_fk"
        const val INSERT_ID = 0
    }
}
