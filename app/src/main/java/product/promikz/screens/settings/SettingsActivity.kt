package product.promikz.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import product.promikz.AppConstants.VERSION_APK
import product.promikz.MyUtils.uToast
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.screens.settings.language.LanguageActivity
import product.promikz.screens.settings.theme.ThemeSettingsActivity
import product.promikz.databinding.ActivitySettingsBinding
import product.promikz.screens.settings.userPrivacy.PrivacyUserActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding


    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.settingsConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.settingsConnect.visibility = View.GONE
            }
        }

        binding.versionPromi.text = "v-$VERSION_APK"

        binding.cons1.setOnClickListener{
//            val intent = Intent(this, ThemeSettingsActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            uToast(this, "Система временно недоступна из-за технических работ.")
        }
        binding.cons2.setOnClickListener{
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.cons3.setOnClickListener{
            val intent = Intent(this, PrivacyUserActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.sBackCard.setOnClickListener {
            onBackPressed()
        }

    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right )
    }
}