package product.promikz.screens.onlineChat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityChatOnlineBinding

class ChatOnlineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatOnlineBinding

    lateinit var database: FirebaseFirestore

    private lateinit var mChatOnlinekFragment: ChatOnlinekFragment

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatOnlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.chatConnect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.chatConnect.visibility = View.GONE
            }
        }

        mChatOnlinekFragment = ChatOnlinekFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerChat, mChatOnlinekFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}