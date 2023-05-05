package product.promikz.screens.pageErrorNetworks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import product.promikz.NetworkConnection
import product.promikz.R

class NetworkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                newIntent()
            }
        }
    }

    private fun newIntent() {
        val intent = Intent(this, product.promikz.MainActivity::class.java)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}