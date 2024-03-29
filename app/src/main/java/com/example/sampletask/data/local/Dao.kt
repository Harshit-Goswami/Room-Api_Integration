package com.example.sampletask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sampletask.model.Data
import com.example.sampletask.model.UserResponse

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertData(userEntity: UserEntity)

//WHERE personalcontact =:personalcontact"
    @Query("SELECT * FROM Login")
    fun getUser() : UserEntity
}