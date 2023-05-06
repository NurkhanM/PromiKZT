package product.promikz.screens.notifications.show

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import product.promikz.NetworkConnection
import product.promikz.R
import product.promikz.databinding.ActivityNotificationShowBinding

class NotificationShowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationShowBinding
    private lateinit var fragment: NotificationShowFragment
    private lateinit var notifType: String
    private lateinit var idNotif: String

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parseIntent()
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
        fragment = if (notifType == NOTIFICATION_REPORT)
            NotificationShowFragment.newInstanceReportNotification()
        else
            NotificationShowFragment.newInstancePromiNotification()

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

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_TYPE))
            throw RuntimeException("param notification type is absent")
        idNotif = intent.getStringExtra("idNotif").toString()
        notifType = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE).toString()

    }

    companion object {
        private const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"
        private const val NOTIFICATION_PROMI = "promi"
        private const val NOTIFICATION_REPORT = "report"

        //todo спросить зачем IDnotif передается если не используется
        fun newIntentReportNotification(context: Context, baseID: String): Intent {
            val intent = Intent(context, NotificationShowActivity::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_TYPE, NOTIFICATION_REPORT)
            intent.putExtra("idNotif", baseID)
            return intent
        }

        fun newIntentPromiNotification(context: Context, baseID: String): Intent {
            val intent = Intent(context, NotificationShowActivity::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_TYPE, NOTIFICATION_PROMI)
            intent.putExtra("idNotif", baseID)
            return intent
        }
    }

}