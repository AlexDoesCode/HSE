package hse24.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hse24.db.entity.CartEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ENTITY_ID)
    val id: Int,

    @ColumnInfo(name = SKU)
    val sku: Int,

    @ColumnInfo(name = NAME)
    val name: String,

    @ColumnInfo(name = BRAND_NAME)
    val brand: String,

    @ColumnInfo(name = IMAGE_URL)
    val imageUrl: String,

    @ColumnInfo(name = VARIATION_NAME)
    val variation: String,

    @ColumnInfo(name = PRICE)
    val price: Float,

    @ColumnInfo(name = CURRENCY)
    val currency: String
) {

    companion object {
        const val TABLE_NAME = "cart_data"
        const val ENTITY_ID = "entity_id"
        const val SKU = "entity_sku"
        const val NAME = "entity_name"
        const val BRAND_NAME = "brand_name"
        const val IMAGE_URL = "image_uri"
        const val VARIATION_NAME = "variation_desc"
        const val PRICE = "product_price"
        const val CURRENCY = "currency_string"
        const val CART_INSERT_ID = 0
    }
}
