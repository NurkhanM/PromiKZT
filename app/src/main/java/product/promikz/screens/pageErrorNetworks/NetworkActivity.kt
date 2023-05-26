package product.promikz.screens.pageErrorNetworks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityCompanyBinding
import product.promikz.databinding.ActivityNetworkBinding

class NetworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                newIntent()
            }
        }

        binding.nextSettingsNetwork.setOnClickListener {
            nextSettings()
        }
    }

    private fun newIntent() {
        val intent = Intent(this, product.promikz.MainActivity::class.java)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun nextSettings(){
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)
    }
}