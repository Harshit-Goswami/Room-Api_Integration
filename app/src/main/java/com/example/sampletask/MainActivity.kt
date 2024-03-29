package com.example.sampletask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.example.sampletask.data.local.UserEntity
import com.example.sampletask.databinding.ActivityMainBinding
import com.example.sampletask.model.UserResponse
import com.example.sampletask.utils.ApiState
import com.example.sampletask.utils.DbState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {
            val phoneNumber = binding.editTextPhoneNumber.text.toString().trim()

            if (isValidPhoneNumber(phoneNumber)) {
                getUserData(phoneNumber)
            } else {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnGetUser.setOnClickListener {
            getLoggedUser()
        }
        binding.textView.movementMethod = ScrollingMovementMethod()

    }

    private fun getUserData(phoneNumber: String) {
        lifecycleScope.launch {
            mainViewModel.getData(phoneNumber)
            mainViewModel._userStateFlow.collect() {
                when (it) {
                    is ApiState.Empty -> Log.d("API_STATE", "Empty")

                    is ApiState.Failure -> {
                        Toast.makeText(applicationContext, "Error : ${it.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ApiState.Loading -> {
                        Toast.makeText(
                            applicationContext,
                            "Loading.....",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ApiState.Success -> {
                        binding.textView.text = it.data.toString()
                        if (it.data.Data.isNotEmpty()){
                            Toast.makeText(
                                applicationContext,
                                "User Found -${it.data.Data[0].customername}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = it.data.Data[0]
                            saveUserData(
                                UserEntity(
                                    address = i.address,
                                    cityname =
                                    i.cityname,
                                    customerid = i.customerid,
                                    customername = i.customername,
                                    emailid = i.emailid,
                                    gender = i.gender,
                                    password = i.password,
                                    personalcontact = i.personalcontact,
                                    pincode = i.pincode,
                                    statename = i.statename
                                )
                            )
                        }
                        Log.d("API_STATE", "Data : ${it.data}")

                    }
                }
            }
        }
    }

    private fun getLoggedUser() {
        if (databaseFileExists()){
            lifecycleScope.launch(Dispatchers.IO) {
                mainViewModel.getUser()
                mainViewModel._loggedUserStateFlow.collect() {
                    when (it) {
                        is DbState.Empty ->
                            launch(Dispatchers.Main) {

                                Toast.makeText(
                                    applicationContext,
                                    "Empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        is DbState.Failure -> {
                            launch(Dispatchers.Main) {

                                Toast.makeText(
                                    applicationContext,
                                    "Err - ${it.msg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        DbState.Loading -> {
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    applicationContext,
                                    "Loading...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        is DbState.Success -> {
                            launch(Dispatchers.Main) {
                                binding.textView.text = it.data.toString()
                                Toast.makeText(
                                    applicationContext,
                                    "Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
                }
            }
        }else{
            Toast.makeText(
                applicationContext,
                "Database is not Exist!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveUserData(user: UserEntity) {
        mainViewModel.insert(user)
        Toast.makeText(
            applicationContext,
            "User Saved to database",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.length != 10) {
            false
        } else {
            val firstChar = phoneNumber[0]
            firstChar != '0'
        }
    }
    private fun databaseFileExists(): Boolean {
        return try {
            File(getDatabasePath("UserDatabase").absolutePath).exists()
        }catch (e: Exception){
            false
        }
    }
}