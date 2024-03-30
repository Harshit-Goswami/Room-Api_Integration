package com.example.sampletask

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import com.example.sampletask.data.local.UserEntity
import com.example.sampletask.databinding.ActivityMainBinding
import com.example.sampletask.databinding.DialogLoginBinding
import com.example.sampletask.databinding.DialogProfileBinding
import com.example.sampletask.model.Data
import com.example.sampletask.utils.ApiState
import com.example.sampletask.utils.DbState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
   lateinit var profileBinding : DialogProfileBinding
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
        binding.loggedUser.setOnClickListener {
            getLoggedUser()
        }
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
                        if (it.data.Data.isNotEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "User Found -${it.data.Data[0].customername}",
                                Toast.LENGTH_SHORT
                            ).show()
                            showLoginDialog(it.data.Data[0])
                            Log.d("API_STATE", "Data : ${it.data}")
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "User Not Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        mainViewModel.userStateFlow.value = ApiState.Empty

                    }
                }
            }
        }
    }

    private fun getLoggedUser() {
        if (databaseFileExists()) {
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
                                showProfileDialog(it.data)
                            }

                        }
                    }
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "User Not Found",
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
        } catch (e: Exception) {
            false
        }
    }

    private fun showLoginDialog(data: Data) {
        val loginBinding = DialogLoginBinding.inflate(layoutInflater)
        try {
            val loginDataDialog = AlertDialog.Builder(
                this@MainActivity,
                R.style.CustomAlertDialogLogin
            ).create()
            loginDataDialog.window?.setGravity(Gravity.CENTER)
            loginDataDialog.setCancelable(true)
            loginDataDialog.setView(loginBinding.root)
            loginDataDialog.setCanceledOnTouchOutside(true)
            loginDataDialog.show()

        } catch (e: Exception) {
            Log.d("dialog Error-", "${e.message}")
        }
        val loginMap = data.serializeToMap()
        loginMap.forEach { key, value ->
            val tv = TextView(this)
            tv.setText("$key - $value")
            tv.textSize = 18f
            tv.setPadding(30, 5, 30, 5)
            tv.setTextColor(Color.BLACK)
//            tv.setBackgroundResource(R.drawable.unselected_product_unit_layout)
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(15, 0, 15, 0)
            tv.layoutParams = lp
            loginBinding.dialogLinLayLogin.addView(tv)
        }

        loginBinding.btnGetUser.setOnClickListener {
            saveUserData(
                UserEntity(
                    address = data!!.address,
                    cityname =
                    data.cityname,
                    customerid = data.customerid,
                    customername = data.customername,
                    emailid = data.emailid,
                    gender = data.gender,
                    password = data.password,
                    personalcontact = data.personalcontact,
                    pincode = data.pincode,
                    statename = data.statename
                )
            )
        }
    }

    private fun showProfileDialog(dbUser: UserEntity) {
        profileBinding = DialogProfileBinding.inflate(layoutInflater)
        try {
            val profileDataDialog = AlertDialog.Builder(
                this@MainActivity,
                R.style.CustomAlertDialogLogin
            ).create()
            profileDataDialog.window?.setGravity(Gravity.TOP)
            profileDataDialog.setCancelable(true)
            profileDataDialog.setView(profileBinding.root)
            profileDataDialog.setCanceledOnTouchOutside(true)
            profileDataDialog.show()

        } catch (e: Exception) {
            Log.d("dialog Error-", "${e.message}")
        }
        val dbMap = dbUser.serializeToMap()
        addTextView("Id", dbMap["customerid"].toString())
        addTextView("Name", dbMap["customername"].toString())
        addTextView("Mobile No", dbMap["personalcontact"].toString())
        addTextView("Gender", dbMap["gender"].toString())
        addTextView("Email", dbMap["emailid"].toString())
        addTextView("Address", dbMap["address"].toString())
        addTextView("City Name", dbMap["cityname"].toString())
        addTextView("State Name", dbMap["statename"].toString())
        addTextView("Pincode", dbMap["pincode"].toString())
    }

    fun addTextView(key: String, value: String) {
        val tv = TextView(this)
        tv.setText("$key:  $value")
        tv.textSize = 20f
        tv.setPadding(30, 10, 30, 5)
        tv.setTextColor(Color.BLACK)
        tv.setBackgroundResource(R.drawable.text_view_bg)
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(15, 10, 15, 10)
        tv.layoutParams = lp
        profileBinding.dialogLinLayProfile.addView(tv)
    }

    //data class to map
    fun <T> T.serializeToMap(): Map<String, Any> {
        return convert()
    }

    //convert a map to a data class
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    inline fun <I, reified O> I.convert(): O {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    /*private fun studentChangePasswordSetUp() {

    }
*/
}