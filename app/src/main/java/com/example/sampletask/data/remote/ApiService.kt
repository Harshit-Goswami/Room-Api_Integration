package com.example.sampletask.data.remote

import com.example.sampletask.model.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @GET("api/Verify?")
    suspend fun getData(@Query("mobileno") mobileNo: String): UserResponse
}