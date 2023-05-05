package product.promikz.screens.specialist.info

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityInfoSpesialistBinding

class InfoSpesialistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoSpesialistBinding

    private lateinit var mInfoSpecialistFragment: InfoSpecialistFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoSpesialistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.specialistConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.specialistConnect.visibility = View.GONE
            }
        }

        mInfoSpecialistFragment = InfoSpecialistFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerInfoSpecialistFragment, mInfoSpecialistFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}