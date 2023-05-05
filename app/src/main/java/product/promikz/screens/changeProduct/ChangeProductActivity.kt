package product.promikz.screens.changeProduct

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityChangeProductBinding

class ChangeProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProductBinding

    private lateinit var mChangeProductFragment: ChangeProductFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inflate the included layout

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.changeProductDisconnect.root.visibility = View.GONE
                binding.changeProductConnect.visibility = View.VISIBLE
            } else {
                binding.changeProductConnect.visibility = View.VISIBLE
                binding.changeProductDisconnect.root.visibility = View.GONE
            }
        }

        mChangeProductFragment = ChangeProductFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_change, mChangeProductFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}