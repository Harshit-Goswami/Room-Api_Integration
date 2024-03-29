package com.example.sampletask.reposiitory

import com.example.sampletask.data.local.Dao
import com.example.sampletask.data.local.UserEntity
import com.example.sampletask.data.remote.ApiServiceImp
import com.example.sampletask.model.Data
import com.example.sampletask.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepo @Inject constructor(private val apiServiceImp: ApiServiceImp, val userDao: Dao) {
    fun getData(mobileNo: String): Flow<UserResponse> = flow {
        emit(apiServiceImp.getData(mobileNo))
    }

    suspend fun insert(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.InsertData(user)
    }

    val getUser: Flow<UserEntity> = flow { emit(userDao.getUser()) }
}