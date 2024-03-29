package com.example.sampletask.model

data class UserResponse(
    val Currentdate: String,
    val Data: List<Data>,
    val Data1: List<Any>,
    val Message: String,
    val Status: Int,
    val Success: Boolean,
    val Userid: Int
)