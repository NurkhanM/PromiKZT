package product.promikz.screens.error

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityOldVersionBinding

class OldVersionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldVersionBinding

    private lateinit var oldVersionFragment: OldVersionFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOldVersionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.oldConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.oldConnect.visibility = View.GONE
            }
        }

        oldVersionFragment = OldVersionFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerOld, oldVersionFragment)
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