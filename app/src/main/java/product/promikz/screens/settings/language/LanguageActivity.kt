package product.promikz.screens.settings.language

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import product.promikz.*
import product.promikz.AppConstants.APP_PREFERENCES
import product.promikz.AppConstants.KEY_LANGUAGE
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityLanguageBinding
import java.util.*


class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding

    private lateinit var preferencesLanguage: SharedPreferences

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesLanguage = getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )


        binding.chbEN.setOnClickListener {
            binding.chbKZ.isChecked = false
            binding.chbRU.isChecked = false
        }

        binding.chbKZ.setOnClickListener {
            binding.chbEN.isChecked = false
            binding.chbRU.isChecked = false
        }
        binding.chbRU.setOnClickListener {
            binding.chbEN.isChecked = false
            binding.chbKZ.isChecked = false
        }
        binding.chbRU.isChecked = true
        if (preferencesLanguage.getString(KEY_LANGUAGE, "ru").toString() == "ru") {
            binding.chbRU.isChecked = true
            binding.chbEN.isChecked = false
            binding.chbKZ.isChecked = false
        }
        if (preferencesLanguage.getString(KEY_LANGUAGE, "ru").toString() == "en") {
            binding.chbRU.isChecked = false
            binding.chbEN.isChecked = true
            binding.chbKZ.isChecked = false
        }
        if (preferencesLanguage.getString(KEY_LANGUAGE, "ru").toString() == "kk") {
            binding.chbRU.isChecked = false
            binding.chbEN.isChecked = false
            binding.chbKZ.isChecked = true
        }


        binding.btnSettingSave.setOnClickListener {
            if (binding.chbEN.isChecked) {
                preferencesLanguage.edit().putString(KEY_LANGUAGE, "en").apply()
                langChange("en")
                newIntent()
            } else
                if (binding.chbKZ.isChecked) {
                    preferencesLanguage.edit().putString(KEY_LANGUAGE, "kk").apply()
                    langChange("kk")
                    newIntent()

                } else
                    if (binding.chbRU.isChecked) {
                        preferencesLanguage.edit().putString(KEY_LANGUAGE, "ru").apply()
                        langChange("ru")
                        newIntent()
                    }
        }

        binding.sBackCard.setOnClickListener {
            onBackPressed()
        }

    }

    private fun newIntent() {
        val intent = Intent(this, MainActivity::class.java)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    private fun langChange(langCode: String?) {
        val manager = SplitInstallManagerFactory.create(this)


        val request = SplitInstallRequest.newBuilder()
            .addLanguage(Locale.forLanguageTag(langCode.toString()))
            .build()

        manager.startInstall(request).addOnSuccessListener {
            val locale = Locale(langCode.toString())
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
            recreate()
        }.addOnFailureListener { exception ->
            uToast(this, "Произошла ошибка при смене языка $exception")
        }


    }


}