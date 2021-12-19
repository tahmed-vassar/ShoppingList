package com.example.andcart2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.andcart2.R
import java.io.Serializable

@Entity(tableName = "itemTable")
data class Item(
    @PrimaryKey(autoGenerate = true) var _id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "category") var categoryIDX: Int,
    @ColumnInfo(name = "price") var price: Float,
    @ColumnInfo(name = "bought") var bought: Boolean,
    @ColumnInfo(name = "description") var description: String

    ) : Serializable {
    companion object {

        val categories = listOf<String>("Canned Goods", "Dairy", "Produce", "Meat", "Bread")


        var categoryMap = mapOf<Int, Int>(0 to R.drawable.canned,
                                        1 to R.drawable.dairy,
                                        2 to R.drawable.produce,
                                        3 to R.drawable.meat,
                                        4 to R.drawable.bread)
    }

    fun getCategory(position: Int): String{
        return categories[position]

    }

    fun getImageResource(item: Item): Int {
        return categoryMap[item.categoryIDX]!!
    }


}