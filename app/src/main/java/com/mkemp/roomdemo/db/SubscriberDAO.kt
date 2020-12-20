package com.mkemp.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubscriberDAO {

    // suspend function for use with coroutines
    // returns row id
    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    // returns rows updated
    @Update
    suspend fun updateSubscriber(subscriber: Subscriber): Int

    // returns rows deleted
    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber): Int

    @Query("DELETE FROM subscriber_data_table")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers(): LiveData<List<Subscriber>>

}