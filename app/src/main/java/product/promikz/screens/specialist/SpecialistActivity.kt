package product.promikz.screens.specialist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivitySpecialistBinding
import product.promikz.databinding.ItemSpecialistModelsBinding

class SpecialistActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpecialistBinding
    private lateinit var bindingAdapter: ItemSpecialistModelsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecialistBinding.inflate(layoutInflater)
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


    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right )
    }

}