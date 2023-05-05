package product.promikz.screens.complaint.store


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityComplaintBinding
import product.promikz.databinding.ActivityStateConfirmBinding

class ComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintBinding

    private lateinit var mComplaintUserFragment: ComplaintUserFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintBinding.inflate(layoutInflater)
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

        mComplaintUserFragment = ComplaintUserFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerReport, mComplaintUserFragment)
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