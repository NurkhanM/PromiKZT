package product.promikz.screens.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityShopsBinding

class ShopsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.userConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.userConnect.visibility = View.GONE
            }
        }

    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right )
    }
}