package product.promikz.screens.subscriber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityCompanyBinding
import product.promikz.databinding.ActivityNotificationsBinding
import product.promikz.databinding.ActivitySubscriberBinding
import product.promikz.screens.company.CompanyFragment
import product.promikz.screens.notifications.index.NotificationsFragment

class SubscriberActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriberBinding
    private lateinit var fragment: SubsriberFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscriber)
        binding = ActivitySubscriberBinding.inflate(layoutInflater)
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

        fragment = SubsriberFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerCompany, fragment)
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