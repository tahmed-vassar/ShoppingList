package com.example.andcart2.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDAO {

    @Query("SELECT * FROM itemTable")
    fun getAllItem() : LiveData<List<Item>>

    @Insert
    fun addItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: Item)


    @Query("DELETE FROM itemTable")
    fun deleteAllItems()


    @Query("SELECT SUM(price)  FROM itemTable")
    fun calcPrice() : LiveData<Float?>

}