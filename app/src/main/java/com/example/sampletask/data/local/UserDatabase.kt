package com.example.sampletask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sampletask.model.Data

@Database(entities = arrayOf(UserEntity::class), version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun loginDao(): Dao
}