package com.example.sampletask.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Login")
data class UserEntity(
    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "cityname")
    var cityname: String,

    @PrimaryKey
    @ColumnInfo(name = "customerid")
    var customerid: Int,

    @ColumnInfo(name = "customername")
    var customername: String,

    @ColumnInfo(name = "emailid")
    var emailid: String,

    @ColumnInfo(name = "gender")
    var gender: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "personalcontact")
    var personalcontact: Long,

    @ColumnInfo(name = "pincode")
    var pincode: Int,

    @ColumnInfo(name = "statename")
    var statename: String,

)