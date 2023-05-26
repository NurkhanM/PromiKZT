package product.promikz

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import product.promikz.AppConstants.VERSION_APK
import product.promikz.databinding.ActivityBaseBinding
import product.promikz.screens.auth.AuthActivity
import product.promikz.screens.error.OldVersionActivity
import product.promikz.screens.pageErrorNetworks.ServerErrorActivity
import product.promikz.viewModels.HomeViewModel


class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding


    private lateinit var viewModel: HomeViewModel

    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.visibility = View.GONE
                binding.companyConnect.visibility = View.VISIBLE


                dialog = Dialog(this)
                viewModel.getVersion()
                viewModel.myVersion.observe(this) { list ->
                    if (list.isSuccessful) {

                        if (list.body()?.data?.get(2)?.value == "true") {

                            if (VERSION_APK == list.body()?.data?.get(0)?.value) {

                                continueApp()

                            } else {

                                alertDialogVersion()

                            }
                        } else {
                            serverErrorNext()
                        }

                    } else {
                        serverErrorNext()
                    }
                }


            } else {
                binding.disconnect.visibility = View.VISIBLE
                binding.companyConnect.visibility = View.GONE

                // Inside onCreate() function
                binding.nextSettingsNetwork.setOnClickListener {
                    nextSettings()
                }

            }
        }
    }


    private fun nextSettings() {
        MyUtils.uLogD("Click error")
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)
    }


    private fun serverErrorNext() {
        val intent = Intent(this, ServerErrorActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.zoom_enter,
            R.anim.zoom_exit
        )
        finish()
    }


    @SuppressLint("SetTextI18n")
    private fun alertDialogVersion() {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_pay)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_pay_yes)
        val buttonNOT = dialog.findViewById<Button>(R.id.dialog_pay_no)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_title_pay)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_pay)
        textTitle.text = "Обновления"
        textDescrip.text = "Вышла новая версия PromiKZ \nХотите обновить?"
        buttonYES.text = "Обновить"
        buttonNOT.text = "Не обновлять"
        buttonYES.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
            dialog.dismiss()
        }
        buttonNOT.setOnClickListener {
            continueApp()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun continueApp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val intent = Intent(baseContext, AuthActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            finish()

        } else {

            val intent = Intent(baseContext, OldVersionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            finish()
        }


    }
}