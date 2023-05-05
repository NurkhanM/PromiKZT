package product.promikz.screens.create.newShop

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityShopCreatBinding

class ShopCreateActivity : AppCompatActivity() {

    private lateinit var mShopCreateFragment: ShopCreateFragment

    private lateinit var binding: ActivityShopCreatBinding


    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopCreatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.shopCreateConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.shopCreateConnect.visibility = View.GONE
            }
        }

        mShopCreateFragment = ShopCreateFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerShopCreate, mShopCreateFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}