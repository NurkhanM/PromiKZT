package product.promikz.screens.hometwo.gaid

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityGaidBinding
import product.promikz.screens.company.CompanyFragment

class GaidActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGaidBinding

    private lateinit var mCompanyFragment: GaidInfoFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.companyConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.companyConnect.visibility = View.GONE
            }
        }

        mCompanyFragment = GaidInfoFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerCompany, mCompanyFragment)
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