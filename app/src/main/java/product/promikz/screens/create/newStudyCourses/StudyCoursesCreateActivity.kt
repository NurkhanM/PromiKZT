package product.promikz.screens.create.newStudyCourses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityStudyCoursesCreateBinding

class StudyCoursesCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyCoursesCreateBinding

    private lateinit var mStudyCoursesCreateFragment: StudyCoursesCreateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudyCoursesCreateBinding.inflate(layoutInflater)
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

        mStudyCoursesCreateFragment = StudyCoursesCreateFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_create_product, mStudyCoursesCreateFragment)
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