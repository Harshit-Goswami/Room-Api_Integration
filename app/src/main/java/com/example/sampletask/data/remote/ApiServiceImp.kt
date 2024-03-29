package com.example.sampletask.data.remote

import com.example.sampletask.model.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ApiServiceImp  @Inject constructor(val apiService: ApiService){
    suspend fun getData(mobileNo: String): UserResponse = apiService.getData(mobileNo)
}