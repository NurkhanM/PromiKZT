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
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import product.promikz.AppConstants.VERSION_APK
import product.promikz.screens.auth.AuthActivity
import product.promikz.screens.error.OldVersionActivity
import product.promikz.screens.pageErrorNetworks.ServerErrorActivity
import product.promikz.viewModels.HomeViewModel


class BaseActivity : AppCompatActivity() {


    private lateinit var viewModel: HomeViewModel

    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
//        handleIntent(intent)

        dialog = Dialog(this)
        viewModel.getVersion()
        viewModel.myVersion.observe(this) { list ->
            if (list.isSuccessful) {


                if (VERSION_APK == list.body()?.data?.get(0)?.value) {

                    continueApp()

                } else {

                    alertDialogVersion()

                }
            } else {
                val intent = Intent(this, ServerErrorActivity::class.java)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                finish()
            }
        }

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


//    @SuppressLint("MissingSuperCall")
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        val mediaSession = MediaSessionCompat(this, "tag")
//        handleIntent(intent)
    }

//    private fun handleIntent(intent: Intent?) {
//        if (intent?.action == Intent.ACTION_VIEW) {
//            val data: Uri? = intent.data
//            if (data != null && data.host == "promi.kz") {
//                val pathSegments = data.pathSegments
//                if (pathSegments.size >= 2 && pathSegments[0] == "product") {
//                    val id = pathSegments[1] // получить значение id
//                    val myIntent = Intent(this, LinkActivity::class.java)
//                    myIntent.putExtra("id", id) // передать значение id в активити LinkActivity
//                    startActivity(myIntent)
//                }
//            }
//        }
//    }

//}