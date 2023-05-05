package product.promikz.screens.pay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityPayBinding

class PayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.ShowConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.ShowConnect.visibility = View.GONE
            }
        }


    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.zoom_back, R.anim.zoom_out )
    }
}