package product.promikz.screens.kursy

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants
import product.promikz.AppConstants.FILTERS_TYPE
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.databinding.FragmentKursyBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.screens.home.TovarAdapterHome
import product.promikz.screens.search.products.SSortActivity
import product.promikz.screens.update.UpdateActivity
import product.promikz.viewModels.HomeViewModel

class KursyFragment : Fragment() {

    private var _binding: FragmentKursyBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: TovarAdapterHome
    private lateinit var viewModel: HomeViewModel


    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentKursyBinding.inflate(inflater, container, false)
        val view = binding

        recyclerView = view.rvHome
        val space = 30
        val includeEdge = true
        recyclerView.addItemDecoration(SpaceItemDecoration(space, includeEdge))
        adapter = TovarAdapterHome(object : IClickListnearHomeFavorite {
            override fun clickListener(
                baseID: Int,
                viewAdapter: ItemMakeupModelsBinding,
                isLike: Boolean,
                position: Int
            ) {
                if (!isLike) {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                } else {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                }
                viewModel.postLike("Bearer ${AppConstants.TOKEN_USER}", baseID)

            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

                if (adver != 3) {
                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
                    intent.putExtra("hello", baseID)
                    startActivity(intent)
                    (activity as AppCompatActivity).overridePendingTransition(
                        R.anim.zoom_enter,
                        R.anim.zoom_exit
                    )

                } else {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
                        startActivity(browserIntent)
                    }catch (e: ActivityNotFoundException){
                        MyUtils.uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

        })

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        view.swipeRefreshLayout.setOnRefreshListener {
            onResume()
            view.swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    view.moveToBackAdapter.visibility = View.VISIBLE
                } else {
                    view.moveToBackAdapter.visibility = View.GONE
                }
            }

        })

        view.moveToBackAdapter.setOnClickListener {
            viewModel.getKursy("Bearer ${AppConstants.TOKEN_USER}")
            recyclerView.smoothScrollToPosition(0)
        }

        view.hhBackCard.setOnClickListener {
            AppConstants.BRAND_INT_FILTERS_DATA = 0
            activity?.onBackPressed()
        }

        view.imgKursyFilters.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            FILTERS_TYPE = "2"
        }
        return view.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            viewModel.getKursy("Bearer ${AppConstants.TOKEN_USER}")
            viewModel.myKursy.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful) {
                    recyclerView.removeAllViewsInLayout()
                    adapter.setList(list.body()?.data!!)
                    adapter.notifyDataSetChanged()
                }

            }

        } catch (e: ApiException) {
            e.printStackTrace()
            binding?.dopText?.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}