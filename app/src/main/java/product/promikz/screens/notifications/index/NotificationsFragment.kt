package product.promikz.screens.notifications.index

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.totalNotification
import product.promikz.R
import product.promikz.databinding.FragmentNotificationsBinding
import product.promikz.inteface.IClickListnearNotification
import product.promikz.screens.notifications.index.NotificationAdapter.Companion.VIEW_TYPE_PROMI
import product.promikz.screens.notifications.index.NotificationAdapter.Companion.VIEW_TYPE_UNREADED
import product.promikz.screens.notifications.show.NotificationShowActivity
import product.promikz.viewModels.HomeViewModel


class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNotification: HomeViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: NotificationAdapter

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        mNotification = ViewModelProvider(this)[HomeViewModel::class.java]

        //todo Sdelat normalno
        totalNotification = 0

        adapter = NotificationAdapter(object : IClickListnearNotification {
            override fun clickListener(
                baseID: String,
                position: Int,
                holder: RecyclerView.ViewHolder
            ) {
                var notifType = VIEW_TYPE_PROMI
                when (holder) {
                    is NotificationAdapter.MyViewHolderReaded -> {
                        notifType = holder.notifType

                    }
                    is NotificationAdapter.MyViewHolderUnreaded -> {
                        notifType = holder.notifType
                        if (holder.readType == VIEW_TYPE_UNREADED)
                            holder.binding.ivUnreaded.visibility = View.GONE
                    }
                }
                val intent = if (notifType == VIEW_TYPE_PROMI)
                    NotificationShowActivity.newIntentPromiNotification(
                        requireContext(),
                        baseID
                    )
                else
                    NotificationShowActivity.newIntentReportNotification(
                        requireContext(),
                        baseID
                    )
                startActivity(intent)


            }
        })
        with(binding) {
            recyclerView = rvFavoritesActivity
            recyclerView.adapter = adapter
            nBackCard.setOnClickListener {
                activity?.onBackPressed()
                activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            swipeRefreshLayoutFavorites.setOnRefreshListener {
                ref()
            }
        }
        ref()
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    fun ref() {
        mNotification.getNotification("Bearer $TOKEN_USER")
        mNotification.myNotificationList.observe(viewLifecycleOwner) { list ->
            with(binding) {
                if (list.isSuccessful) {
                    list.body()?.data?.let { adapter.setData(it) }
                    swipeRefreshLayoutFavorites.isRefreshing = false
                } else {
                    swipeRefreshLayoutFavorites.visibility = View.GONE
                    FavoritesWorkError.visibility = View.VISIBLE
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

}