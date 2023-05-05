package product.promikz.screens.settings.userPrivacy

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import product.promikz.AppConstants.PRIVACY_AGREEMENT
import product.promikz.AppConstants.PRIVACY_USER
import product.promikz.R
import product.promikz.databinding.ActivityPrivacyUserBinding

class PrivacyUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyUserBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyUserBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.cons1.setOnClickListener{
            inToEmail(PRIVACY_AGREEMENT)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.cons2.setOnClickListener{
            inToEmail(PRIVACY_USER)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.sBackCard.setOnClickListener {
            onBackPressed()
        }


    }

    @Suppress("DEPRECATION")
    private fun inToEmail(url: String) {
        val address = Uri.parse(url)
        val openlink = Intent(Intent.ACTION_VIEW, address)
        try {
            startActivity(openlink)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, resources.getText(R.string.no_select), Toast.LENGTH_SHORT).show()
            // Define what your app should do if no activity can handle the intent.
        }

    }
}