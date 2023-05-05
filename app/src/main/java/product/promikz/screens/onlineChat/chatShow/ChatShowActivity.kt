package product.promikz.screens.onlineChat.chatShow

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityChatShowBinding

class ChatShowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatShowBinding

    lateinit var database: FirebaseFirestore

    private lateinit var mChatShowFragment: ChatShowFragment
    @SuppressLint("CommitTransaction")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.chatShowConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.chatShowConnect.visibility = View.GONE
            }
        }

        mChatShowFragment = ChatShowFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerChatShow, mChatShowFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        binding.chatShowBack.setOnClickListener {
            onBackPressed()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}