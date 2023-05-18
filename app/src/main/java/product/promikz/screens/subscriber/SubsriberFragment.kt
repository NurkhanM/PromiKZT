package product.promikz.screens.subscriber

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.AppConstants
import product.promikz.AppConstants.MAP_SEARCH
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.categoryState
import product.promikz.R
import product.promikz.databinding.FragmentSubsriberBinding
import product.promikz.inteface.IClickListnearSubscriber
import product.promikz.screens.search.products.ssortInfo.SsortInfoActivity
import product.promikz.screens.update.userOther.UserOtherActivity
import product.promikz.viewModels.HomeViewModel

class SubsriberFragment : Fragment() {

    private var _binding: FragmentSubsriberBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNotification: HomeViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: SubscriberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mNotification = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentSubsriberBinding.inflate(inflater, container, false)



        mNotification.subIndex("Bearer $TOKEN_USER")


        adapter = SubscriberAdapter(object : IClickListnearSubscriber {
            override fun clickListener(
                baseID: Int,
                position: Int,
                holder: RecyclerView.ViewHolder,
                type: String,
                name: String
            ) {

                if (isNameNotification(type) == "Shop"){
                    val intent = Intent(requireActivity(), UserOtherActivity::class.java)
                    intent.putExtra("User2_1", baseID)
                    startActivity(intent)
                }else{
                    MAP_SEARCH["category"] = baseID.toString()
                    categoryState = true
                    val intent = Intent(requireActivity(), SsortInfoActivity::class.java)
                    intent.putExtra("subCategory", name)
                    startActivity(intent)
                }


                activity?.overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )



            }

            override fun clickListenerOtpiska(baseID: Int, name: String, position: Int) {

                if (name == "Category"){
                    mNotification.subCategory("Bearer $TOKEN_USER", baseID)
                }else{
                    mNotification.subShop("Bearer $TOKEN_USER", baseID)
                }
                adapter.deleteMyEducations(position)
//                mNotification.subIndex("Bearer $TOKEN_USER")
//                ref()
            }
        })

        with(binding) {
            recyclerView = rvSubscriber
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

    private fun isNameNotification(str: String): String {
        return when (str) {
            "App\\Models\\Shop" -> {
                return "Shop"
            }
            "App\\Notifications\\Category" -> {
                return "Category"
            }

            else -> "Error"
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun ref() {
        mNotification.myIndexSubscriber.observe(viewLifecycleOwner) { list ->
            with(binding) {
                if (list.isSuccessful) {
                    list.body()?.data?.let { adapter.setData(it) }
                    swipeRefreshLayoutFavorites.isRefreshing = false
                    binding.swipeRefreshLayoutFavorites.visibility = View.VISIBLE
                    binding.FavoritesWorkError.visibility = View.GONE
                    if (list.body()?.data?.isEmpty() == true){
                        swipeRefreshLayoutFavorites.visibility = View.GONE
                        FavoritesWorkError.visibility = View.VISIBLE
                    }
                } else {
                    swipeRefreshLayoutFavorites.visibility = View.GONE
                    FavoritesWorkError.visibility = View.VISIBLE
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}