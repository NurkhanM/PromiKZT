package product.promikz.screens.complaint.specialist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityComplaintSpecialistBinding
import product.promikz.databinding.ActivityTradeInCreateBinding

class ComplaintSpecialistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintSpecialistBinding


    private lateinit var fragment: ComplaintSpecialistFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintSpecialistBinding.inflate(layoutInflater)
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

        fragment = ComplaintSpecialistFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerReport, fragment)
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