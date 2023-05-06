package product.promikz.screens.notifications.show

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.databinding.FragmentNotificationPromiShowBinding
import product.promikz.databinding.FragmentNotificationReportShowBinding
import product.promikz.models.notification.show.NotificationShowModels
import product.promikz.viewModels.HomeViewModel
import retrofit2.Response

class NotificationShowFragment : Fragment() {
    private var _bindingPromi: FragmentNotificationPromiShowBinding? = null

    private var _bindingReport: FragmentNotificationReportShowBinding? = null
    private val bindingPromi get() = _bindingPromi

    private val bindingReport get() = _bindingReport
    private var idNotif = ""
    private lateinit var vModels: HomeViewModel
    private lateinit var notificationType: String

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vModels = ViewModelProvider(this)[HomeViewModel::class.java]
        parseParams()
        if (notificationType == NOTIFICATION_PROMI)
            _bindingPromi =
                FragmentNotificationPromiShowBinding.inflate(inflater, container, false)
        else {
            _bindingReport =
                FragmentNotificationReportShowBinding.inflate(inflater, container, false)
        }
        val arguments = (activity as AppCompatActivity).intent.extras
        idNotif = arguments!!["idNotif"] as String
        vModels.showNotification("Bearer $TOKEN_USER", idNotif)
        vModels.myShowNotification.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                initViews(list)
            } else {
                initErrorView()
            }
        }

        getBindingBackBtn().setOnClickListener {
            activity?.onBackPressed()
        }
        return if (notificationType == NOTIFICATION_PROMI)  _bindingPromi!!.root else  _bindingReport!!.root
    }

    fun getBindingBackBtn(): ImageView {
        return if (_bindingPromi != null)
            bindingPromi!!.backBtn
        else {
            bindingReport!!.backBtn
        }
    }

    fun initViews(list: Response<NotificationShowModels>) {
        if (notificationType == NOTIFICATION_PROMI) {
            with(bindingPromi!!) {
                val sender = list.body()?.data?.user?.name
                tvTitle.text = list.body()?.data?.data?.title
                tvSender.text = sender
                tvMessage.text = list.body()?.data?.data?.text
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }
            Glide.with(this)
                .load(list.body()?.data?.user?.img)
                .placeholder(R.drawable.ic_baseline_storefront_24)
                .error(R.drawable.image_android_error)
                .override(40, 40)
                .centerCrop()
                .into(bindingPromi!!.tvIcon)
        } else {
            with(bindingReport!!) {
                val sender = list.body()?.data?.user?.name
                tvTitle.text = list.body()?.data?.data?.title
                tvSender.text = sender
                tvMessage.text = list.body()?.data?.data?.text
                tvComplaint.text = isNameNotification(list.body()?.type.toString())
                tvComplaintDescrip.text = "-> ${list.body()?.data?.report?.description}"
                date.text = list.body()?.created_at
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }
            Glide.with(this)
                .load(list.body()?.data?.user?.img)
                .placeholder(R.drawable.ic_baseline_storefront_24)
                .error(R.drawable.image_android_error)
                .override(40, 40)
                .centerCrop()
                .into(bindingReport!!.tvIcon)
            Glide.with(this)
                .load(list.body()?.data?.notifiable?.img)
                .placeholder(R.drawable.ic_baseline_storefront_24)
                .error(R.drawable.image_android_error)
                .override(80, 80)
                .centerCrop()
                .into(bindingReport!!.ivShopItem)
        }
    }

    fun initErrorView() {
        if (notificationType == NOTIFICATION_PROMI) {
            with(bindingPromi!!) {
                cons2.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE
                line.visibility = View.GONE
                ivFavorite.visibility = View.GONE
            }
        } else {
            with(bindingReport!!) {
                cons2.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE
                line.visibility = View.GONE
                ivFavorite.visibility = View.GONE
            }

        }

    }


    private fun isNameNotification(str: String): String {

        return when (str) {
            "App\\Notifications\\NotificationUsersNotify" -> {
                return "PromiKZ"
            }
            "App\\Notifications\\Report" -> {
                return "Ответ на жалабу"
            }
            else -> "Error"
        }
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(NOTIFICATION_TYPE)) {
            throw RuntimeException("param Notification type is absent")
        }
        notificationType = args.getString(NOTIFICATION_TYPE).toString()
        if (notificationType != NOTIFICATION_PROMI && notificationType != NOTIFICATION_REPORT)
            throw RuntimeException("unknown notification type $notificationType")
    }

    companion object {
        const val NOTIFICATION_TYPE = "notification"
        const val NOTIFICATION_REPORT = "report"
        const val NOTIFICATION_PROMI = "promi"
        fun newInstanceReportNotification(): NotificationShowFragment {
            return NotificationShowFragment().apply {
                arguments = Bundle().apply {
                    putString(NOTIFICATION_TYPE, NOTIFICATION_REPORT)
                }
            }
        }

        fun newInstancePromiNotification(): NotificationShowFragment {
            return NotificationShowFragment().apply {
                arguments = Bundle().apply {
                    putString(NOTIFICATION_TYPE, NOTIFICATION_PROMI)
                }
            }
        }
    }

}