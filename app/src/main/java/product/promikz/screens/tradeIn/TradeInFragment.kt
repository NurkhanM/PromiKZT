package product.promikz.screens.tradeIn

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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants
import product.promikz.AppConstants.CATEGORY_INT_FILTERS_DATA
import product.promikz.AppConstants.FILTERS_TYPE
import product.promikz.AppConstants.FILTER_INT_ALL
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS
import product.promikz.MainActivity
import product.promikz.MyUtils
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentTradeInBinding
import product.promikz.databinding.FragmentTwoAdvertBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory
import product.promikz.screens.home.categoryfilter.CategoryFiltersAdapter
import product.promikz.screens.search.products.SSortActivity

class TradeInFragment : Fragment() {

    private var _binding: FragmentTradeInBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null

    private var activityBinding2: FragmentTwoAdvertBinding? = null

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCF: RecyclerView
    lateinit var adapter: TovarAdapterHomeTradeIn
    private lateinit var adapterCF: CategoryFiltersAdapter
    private lateinit var vieww: View
    private lateinit var viewModel: HomeViewModel


    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentTradeInBinding.inflate(inflater, container, false)
        val view = binding
        categoryFilterMed()



        recyclerView = view.rvHome
        val space = 30
        val includeEdge = true
        recyclerView.addItemDecoration(SpaceItemDecoration(space, includeEdge))
        adapter = TovarAdapterHomeTradeIn(object : IClickListnearHomeFavorite {
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
                viewModel.postLike("Bearer $TOKEN_USER", baseID)

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

        stateCF()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        view.swipeRefreshLayout.setOnRefreshListener {
            onResume()
            view.swipeRefreshLayout.isRefreshing = false
        }



        view.moveToBackAdapter.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        view.hhBackCard.setOnClickListener {
            AppConstants.BRAND_INT_FILTERS_DATA = 0
            activity?.onBackPressed()
        }
        view.imgHomeFilters.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            FILTERS_TYPE = "1"
        }


        return view.root
    }

    private fun stateCF() {
        viewModel.getCategoryIndex("Bearer $TOKEN_USER")
        viewModel.myCategoryIndex.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                user.body()?.let {
                    adapterCF.setList(it.data)
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            val arInData = ArrayList<product.promikz.models.products.index.Data>()
            viewModel.getTovarTradeIn("Bearer $TOKEN_USER")
            viewModel.myTovarTradeIn.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful) {
                    recyclerView.removeAllViewsInLayout()
                    arInData.clear()
                    list.body()?.data?.forEach { item ->
                        if (item.type == 1 || item.type == 3){
                            arInData.add(item)
                        }
                    }
                    adapter.setList(arInData)
                    adapter.notifyDataSetChanged()
                }

            }

        } catch (e: ApiException) {
            e.printStackTrace()
            binding?.dopText?.visibility = View.VISIBLE
        }

    }


    private fun categoryFilterMed() {
        recyclerViewCF = binding.rvCategoryPage
        adapterCF = CategoryFiltersAdapter(object : IClickListnearHomeFilterCategory {

            override fun clickListener(id: Int, name: String) {
                MAP_FILTERS_PRODUCTS.clear()
                MAP_FILTERS_PRODUCTS["category"] = id.toString()
                CATEGORY_INT_FILTERS_DATA = id
                FILTER_INT_ALL.add(id)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeTwoFragment_to_homeFragmentPlusTradeIn)
            }

        })

        recyclerViewCF.adapter = adapterCF
        recyclerViewCF.setHasFixedSize(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.moveToBackAdapter.visibility = View.VISIBLE
                    activityBinding2?.tabLayout2?.visibility = View.GONE
                } else {
                    binding.moveToBackAdapter.visibility = View.GONE
                    activityBinding2?.tabLayout2?.visibility = View.VISIBLE
                }
            }

        })

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.toAction?.homeBack?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }


}