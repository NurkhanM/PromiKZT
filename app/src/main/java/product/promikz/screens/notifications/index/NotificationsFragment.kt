package product.promikz.screens.notifications.index

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.databinding.FragmentNotificationsBinding
import product.promikz.inteface.IClickListnearNotification


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
        // Inflate the layout for this fragment
        mNotification = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val view = binding
        adapter = NotificationAdapter(object : IClickListnearNotification {

            override fun clickListener(baseID: String, position: Int) {
//                val intent = Intent(requireActivity(), NotificationShowActivity::class.java)
//                intent.putExtra("idNotif", baseID)
//                startActivity(intent)
            }
        })

        recyclerView = view.rvFavoritesActivity
        recyclerView.adapter = adapter

        ref()


        view.nBackCard.setOnClickListener {
            activity?.onBackPressed()
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right )
        }

        view.swipeRefreshLayoutFavorites.setOnRefreshListener {
            ref()

        }

        return view.root
    }


    @SuppressLint("NotifyDataSetChanged")
    fun ref() {
        mNotification.getNotification("Bearer $TOKEN_USER")
        mNotification.myNotificationList.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                list.body()?.data?.let { adapter.setData(it) }
                binding.swipeRefreshLayoutFavorites.isRefreshing = false
            } else {
                binding.swipeRefreshLayoutFavorites.visibility = View.GONE
                binding.FavoritesWorkError.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}