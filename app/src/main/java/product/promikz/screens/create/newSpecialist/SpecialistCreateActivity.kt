package product.promikz.screens.create.newSpecialist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityComplaintBinding
import product.promikz.databinding.ActivitySpecialistCreateBinding

class SpecialistCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpecialistCreateBinding

    private lateinit var mSpeacialistCreateFragment: SpeacialistCreateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecialistCreateBinding.inflate(layoutInflater)
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

        mSpeacialistCreateFragment = SpeacialistCreateFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_create_product, mSpeacialistCreateFragment)
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