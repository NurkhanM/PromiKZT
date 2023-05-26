package product.promikz.screens.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import product.promikz.*
import product.promikz.AppConstants.APP_PREFERENCES
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.KEY_LANGUAGE
import product.promikz.AppConstants.KEY_TOKEN
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_TYPE
import product.promikz.AppConstants.isStatusServer
import product.promikz.AppConstants.priceMAX
import product.promikz.AppConstants.priceMIN
import product.promikz.AppConstants.shopInfo
import product.promikz.AppConstants.totalCountProduct
import product.promikz.AppConstants.totalCountSchool
import product.promikz.AppConstants.totalCountSpecialist
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.pageErrorNetworks.NetworkActivity
import product.promikz.screens.pageErrorNetworks.ServerErrorActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import product.promikz.AppConstants.ID_SHOP_MY
import product.promikz.AppConstants.KEY_ID_SHOP_MY
import product.promikz.AppConstants.MY_SPECIALIST
import product.promikz.AppConstants.U4EB_STATE
import product.promikz.AppConstants.USER_ID
import product.promikz.AppConstants.VERIFY_USER_EMAIL
import product.promikz.AppConstants.VERIFY_USER_PHONE
import product.promikz.AppConstants.totalNotification
import product.promikz.AppConstants.userIDChat
import java.util.*


class SplashFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    private lateinit var preferencesLanguage: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences
    private lateinit var preferencesIdShopUser: SharedPreferences

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

            viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

            preferencesTOKEN = (activity as AppCompatActivity).getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE
            )

            preferencesIdShopUser = (activity as AppCompatActivity).getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE
            )

            TOKEN_USER = preferencesTOKEN.getString(KEY_TOKEN, "null").toString()
            ID_SHOP_MY = preferencesIdShopUser.getInt(KEY_ID_SHOP_MY, -1)



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        activity?.window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
                            APPEARANCE_LIGHT_STATUS_BARS,
                            APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        activity?.window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
                            0,
                            APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                }
            } else {
                activity?.window?.decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity?.window?.statusBarColor = Color.BLACK
            }



            if (isOnline(requireContext())) {
                preferencesLanguage = (activity as AppCompatActivity).getSharedPreferences(
                    APP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                fullScreen()

                viewModel.getState()
                viewModel.myStats.observe(viewLifecycleOwner) { data ->
                    isStatusServer = data.isSuccessful
                    if (data.isSuccessful) {

                        totalCountProduct = data.body()?.data?.totalCountProduct!!
                        totalCountSchool = data.body()?.data?.totalCountSchool!!
                        totalCountSpecialist = data.body()?.data?.totalCountSpecialist!!
                        priceMAX = data.body()?.data?.price?.max!!
//                    priceAVG = data.body()?.data?.price?.avg!!
                        priceMIN = data.body()?.data?.price?.min!!
                        U4EB_STATE = data.body()?.data?.categoryMain?.schools.toString()

                        if (TOKEN_USER != "null") {

                            viewModel.getUser("Bearer $TOKEN_USER")
                            viewModel.myUser.observe(viewLifecycleOwner) { res ->

                                if (res.isSuccessful){
                                    VERIFY_USER_PHONE = res.body()?.data?.phone_verified_at != null
                                    VERIFY_USER_EMAIL = res.body()?.data?.email_verified_at != null

                                    USER_TYPE = res.body()?.data?.type.toString()
                                    USER_ID = res.body()?.data?.id


                                    if (res.body()?.data?.specialist?.id != null){
                                        MY_SPECIALIST = res.body()?.data?.specialist?.id!!
                                    }

                                    totalNotification = res.body()?.data?.unreadNotifications!!

                                    if (res.body()?.data?.shop != null) {
                                        ID_SHOP_MY = res.body()?.data?.shop!!.id
                                        ID_SHOP_USER = res.body()?.data?.shop!!.id
                                        shopInfo = true
                                    }
                                    userIDChat = res.body()?.data?.id


                                }

                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                                activity?.overridePendingTransition(
                                    R.anim.zoom_enter,
                                    R.anim.zoom_exit
                                )
                                activity?.finish()
                            }


                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_splashFragment_to_checkUserFragment)
                            }
                        }

                    } else {
                        serverErrorNext()
                    }

                }

                langChange(preferencesLanguage.getString(KEY_LANGUAGE, "ru").toString())


            } else {
                val intent = Intent(requireActivity(), NetworkActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                (activity as AppCompatActivity).finish()
            }

        return view

    }


    private fun serverErrorNext(){
        val intent = Intent(requireActivity(), ServerErrorActivity::class.java)
        startActivity(intent)
        (activity as AppCompatActivity).overridePendingTransition(
            R.anim.zoom_enter,
            R.anim.zoom_exit
        )
        (activity as AppCompatActivity).finish()
    }


    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    fun fullScreen() {

        val uiOptions = (activity as AppCompatActivity).window.decorView.systemUiVisibility
        var newUiOptions = uiOptions

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        (activity as AppCompatActivity).window.decorView.systemUiVisibility = newUiOptions
    }

    @Suppress("DEPRECATION")
    private fun langChange(langCode: String?) {
        val locale = Locale(langCode!!)
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @Suppress("DEPRECATION")
    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting

    }

}