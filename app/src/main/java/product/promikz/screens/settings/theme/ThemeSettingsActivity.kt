package product.promikz.screens.settings.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import product.promikz.*
import product.promikz.AppConstants.APP_PREFERENCES
import product.promikz.AppConstants.KEY_THEME
import product.promikz.AppConstants.KEY_THEME_SELECTED
import product.promikz.databinding.ActivityThemeSettingsBinding

class ThemeSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeSettingsBinding

    private lateinit var preferencesTheme: SharedPreferences
    private lateinit var preferencesThemeSelected: SharedPreferences

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferencesTheme = getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )
        preferencesThemeSelected = getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        if (preferencesThemeSelected.getString(KEY_THEME_SELECTED, "0").toString() == "1") {
            binding.chkbSettimgs.isChecked = true
            binding.chkbSettimgs2.isChecked = false
            binding.chkbSettimgs3.isChecked = false
        }
        if (preferencesThemeSelected.getString(KEY_THEME_SELECTED, "0").toString() == "2") {
            binding.chkbSettimgs.isChecked = false
            binding.chkbSettimgs2.isChecked = true
            binding.chkbSettimgs3.isChecked = false
        }
        if (preferencesThemeSelected.getString(KEY_THEME_SELECTED, "0").toString() == "3") {
            binding.chkbSettimgs.isChecked = false
            binding.chkbSettimgs2.isChecked = false
            binding.chkbSettimgs3.isChecked = true
        }



        binding.chkbSettimgs.setOnClickListener {
            preferencesTheme.edit().putString(KEY_THEME, "white").apply()
            preferencesThemeSelected.edit().putString(KEY_THEME_SELECTED, "1").apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.chkbSettimgs2.isChecked = false
            binding.chkbSettimgs3.isChecked = false
        }

        binding.chkbSettimgs2.setOnClickListener {
            preferencesTheme.edit().putString(KEY_THEME, "black").apply()
            preferencesThemeSelected.edit().putString(KEY_THEME_SELECTED, "2").apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.chkbSettimgs.isChecked = false
            binding.chkbSettimgs3.isChecked = false
        }

        binding.chkbSettimgs3.setOnClickListener {
            preferencesTheme.edit().putString(KEY_THEME, "system").apply()
            preferencesThemeSelected.edit().putString(KEY_THEME_SELECTED, "3").apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            binding.chkbSettimgs.isChecked = false
            binding.chkbSettimgs2.isChecked = false
        }

        binding.sBackCard.setOnClickListener {
            onBackPressed()
        }
    }
}