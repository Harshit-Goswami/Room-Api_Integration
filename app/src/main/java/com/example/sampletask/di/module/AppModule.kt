package com.example.retrofitmvvm.di.module

import android.content.Context
import androidx.room.Room
import com.example.sampletask.data.local.Dao
import com.example.sampletask.data.local.UserDatabase
import com.example.sampletask.data.remote.ApiService
import com.example.sampletask.reposiitory.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesApiService(): ApiService =
        Retrofit.Builder()
            .baseUrl("http://myapp.dataupload.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    fun providesUserDao(userDatabase: UserDatabase): Dao = userDatabase.loginDao()

    @Provides
    @Singleton
    fun providesUserDatabase(@ApplicationContext context: Context):UserDatabase
            = Room.databaseBuilder(context,UserDatabase::class.java,"UserDatabase").build()

//    @Provides
//    fun providesUserRepository(userDao: Dao) : UserRepo
//            = UserRepo(userDao)

}