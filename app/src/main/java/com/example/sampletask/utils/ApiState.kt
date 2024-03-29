package com.example.sampletask.utils

import com.example.sampletask.model.UserResponse

sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg:Throwable) : ApiState()
    class Success(val data: UserResponse) : ApiState()
    object Empty : ApiState()
}