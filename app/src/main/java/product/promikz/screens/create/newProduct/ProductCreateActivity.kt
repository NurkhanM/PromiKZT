package product.promikz.screens.create.newProduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.R
import product.promikz.NetworkConnection
import product.promikz.databinding.ActivityProductCreateBinding

class ProductCreateActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductCreateBinding

    private lateinit var mProducCreateFragment: ProducCreateFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.createProductConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.createProductConnect.visibility = View.GONE
            }
        }

        mProducCreateFragment = ProducCreateFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_create_product, mProducCreateFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}