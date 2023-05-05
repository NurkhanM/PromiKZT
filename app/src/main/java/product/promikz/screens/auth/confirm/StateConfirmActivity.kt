package product.promikz.screens.auth.confirm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityStateConfirmBinding

class StateConfirmActivity : AppCompatActivity() {

    lateinit var binding: ActivityStateConfirmBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.reportConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.reportConnect.visibility = View.GONE
            }
        }


    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}