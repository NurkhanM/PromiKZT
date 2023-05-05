package product.promikz.screens.link

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import product.promikz.AppConstants
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MainActivity
import product.promikz.MyUtils.uLogD
import product.promikz.MyUtils.uToast
import product.promikz.R
import product.promikz.databinding.ActivityLinkBinding
import product.promikz.screens.refresh.RefreshActivity
import product.promikz.screens.specialist.info.InfoSpesialistActivity
import product.promikz.screens.update.UpdateActivity
import product.promikz.viewModels.HomeViewModel
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinkBinding

    private lateinit var preferencesTOKEN: SharedPreferences

    lateinit var viewModel: HomeViewModel

    @Suppress("DEPRECATION")
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Получить данные из URL-адреса
        val data: Uri? = intent?.data

        uLogD("TEST -> data1 == $data")

        preferencesTOKEN = getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        TOKEN_USER = preferencesTOKEN.getString(AppConstants.KEY_TOKEN, "null").toString()

        binding.consProducNull.visibility = View.GONE


        // Обработка полученных данных
        if (data != null) {
            // Проверяем на действительность true false
            GlobalScope.launch(Dispatchers.IO) {
                val id = data.lastPathSegment?.toIntOrNull()
                myCoroutine(data.toString(), id)
            }

        } else {
            binding.consProducNull.visibility = View.VISIBLE
            uToast(this, "Недействительная ссылка")

        }

        binding.btnExit.setOnClickListener {
            onBackPressed()
        }

    }

    private suspend fun myCoroutine(urlWeb: String, idWeb: Int?) {
        withContext(Dispatchers.IO) {
            if (checkWebLink(urlWeb)) {
                // строка является целым числом
                if (idWeb != null) {
                    if (TOKEN_USER != "null") {

                        val parts = urlWeb.split("/")
                        if (parts.size >= 4) {
                            when (parts[3]) {
                                "product" -> {
                                    runOnUiThread {
                                        val newIntent =
                                            Intent(this@LinkActivity, UpdateActivity::class.java)
                                        newIntent.putExtra("hello", idWeb)
                                        overridePendingTransition(
                                            R.anim.zoom_enter,
                                            R.anim.zoom_exit
                                        )
                                        startActivity(newIntent)
                                        finish()
                                    }
                                }

                                "specialist" -> {
                                    runOnUiThread {
                                        val newIntent = Intent(
                                            this@LinkActivity,
                                            InfoSpesialistActivity::class.java
                                        )
                                        newIntent.putExtra("spec", idWeb)
                                        overridePendingTransition(
                                            R.anim.zoom_enter,
                                            R.anim.zoom_exit
                                        )
                                        startActivity(newIntent)
                                        finish()
                                    }
                                }

                                else -> {
                                    runOnUiThread {
                                        binding.consProducNull.visibility = View.VISIBLE
                                        uToast(this@LinkActivity, "Недействительная ссылка")
                                    }
                                }

                            }


                        }

                    } else {
                        runOnUiThread {
                            val newIntent = Intent(this@LinkActivity, RefreshActivity::class.java)
                            overridePendingTransition(
                                R.anim.zoom_enter,
                                R.anim.zoom_exit
                            )
                            startActivity(newIntent)
                            finish()
                        }
                    }
                } else {
                    // Получить intent из вызывающего Activity
                    runOnUiThread {
                        val newIntent = Intent(this@LinkActivity, MainActivity::class.java)
//                        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                        startActivity(newIntent)
                        finish()
                    }
                }
            } else {
                runOnUiThread {
                    binding.consProducNull.visibility = View.VISIBLE
                    uToast(this@LinkActivity, "Недействительная ссылка")
                }
            }
        }
    }


    private suspend fun checkWebLink(urlString: String): Boolean {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(urlString)
                val connection = withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection
                connection.requestMethod = "HEAD"
                val responseCode = connection.responseCode
                val isOk = responseCode == HttpURLConnection.HTTP_OK
                continuation.resume(isOk)
            }
        }

    }


}