package product.promikz.screens.refresh

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import product.promikz.*
import product.promikz.AppConstants.APP_PREFERENCES
import product.promikz.AppConstants.ID_SHOP_MY
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.KEY_INFO
import product.promikz.AppConstants.KEY_TOKEN
import product.promikz.AppConstants.MY_SPECIALIST
import product.promikz.AppConstants.SPECIALIST_ALL
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_TYPE
import product.promikz.AppConstants.compareAll
import product.promikz.AppConstants.getSpecialistIDSTATE
import product.promikz.AppConstants.shopInfo
import product.promikz.AppConstants.userIDChat

class RefreshActivity : AppCompatActivity() {

    private lateinit var preferencesInfo: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refresh)


        preferencesInfo = getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )
        preferencesTOKEN = getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        preferencesInfo.edit().putString(KEY_INFO, "null").apply()
        preferencesTOKEN.edit().putString(KEY_TOKEN, "null").apply()

        TOKEN_USER = "null"

        userIDChat = -1
        ID_SHOP_USER = -1
        ID_SHOP_MY = -1

        MY_SPECIALIST = -1
        SPECIALIST_ALL = -1
        getSpecialistIDSTATE = false

        USER_TYPE = ""
        shopInfo = false
        compareAll.clear()
        refreshNextIntent()


    }

    private fun refreshNextIntent() {
        val intent = Intent(this, BaseActivity::class.java)
        startActivity(intent)
        finish()
    }

}