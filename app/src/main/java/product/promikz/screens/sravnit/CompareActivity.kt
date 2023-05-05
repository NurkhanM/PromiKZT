package product.promikz.screens.sravnit

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityCompareBinding

class CompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareBinding

    private lateinit var mCompareFragment: CompareFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.compareConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.compareConnect.visibility = View.GONE
            }
        }

        mCompareFragment = CompareFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerCompare, mCompareFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right )
    }
}