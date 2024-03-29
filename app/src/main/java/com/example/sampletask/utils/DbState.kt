package com.example.sampletask.utils

import com.example.sampletask.data.local.UserEntity
import com.example.sampletask.model.Data
import com.example.sampletask.model.UserResponse

sealed class DbState{
    object Loading : DbState()
    class Failure(val msg:Throwable) : DbState()
    class Success(val data: UserEntity) : DbState()
    object Empty : DbState()
}
