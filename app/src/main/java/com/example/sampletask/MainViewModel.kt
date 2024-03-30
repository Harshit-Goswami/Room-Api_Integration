package com.example.sampletask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampletask.data.local.UserEntity
import com.example.sampletask.model.Data
import com.example.sampletask.model.UserResponse
import com.example.sampletask.reposiitory.UserRepo
import com.example.sampletask.utils.ApiState
import com.example.sampletask.utils.DbState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {
     val userStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val _userStateFlow: StateFlow<ApiState> = userStateFlow

    private val loggedUserStateFlow: MutableStateFlow<DbState> = MutableStateFlow(DbState.Empty)
    val _loggedUserStateFlow: StateFlow<DbState> = loggedUserStateFlow

    fun getData(mobileNo: String) = viewModelScope.launch {
        userStateFlow.value = ApiState.Loading
        userRepo.getData(mobileNo)
            .catch { e ->
                userStateFlow.value = ApiState.Failure(e)
            }.collect { data ->
                userStateFlow.value = ApiState.Success(data)
            }
    }

    fun getUser() = viewModelScope.launch (Dispatchers.IO) {
        loggedUserStateFlow.value = DbState.Loading
        userRepo.getUser
            .catch { e ->
                loggedUserStateFlow.value = DbState.Failure(e)
            }.collect { data ->
                loggedUserStateFlow.value = DbState.Success(data)
            }
    }

    fun insert(user: UserEntity) {
        viewModelScope.launch {
            userRepo.insert(user)
        }
    }
}