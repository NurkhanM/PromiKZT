package product.promikz.screens.tradeIn

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import product.promikz.*
import product.promikz.AppConstants.CATEGORY_INT_FILTERS_DATA
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import com.google.android.gms.common.api.ApiException
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants.FILTER_INT_ALL
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS
import product.promikz.AppConstants.STATE_INT_FILTERS
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentHomePlusBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory
import product.promikz.models.products.index.Data
import product.promikz.screens.home.categoryfilter.CategoryFiltersAdapterPlus
import product.promikz.screens.search.products.SSortActivity


class HomeFragmentPlusTradeIn : Fragment() {

    private var _binding: FragmentHomePlusBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCF: RecyclerView
    lateinit var adapter: TovarAdapterHomeTradeIn
    private lateinit var adapterCF: CategoryFiltersAdapterPlus
    private lateinit var vieww: View
    private lateinit var viewModel: HomeViewModel

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomePlusBinding.inflate(inflater, container, false)
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
                        uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

        })
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        if (STATE_INT_FILTERS){
            viewModel.getCategoryID("Bearer $TOKEN_USER", CATEGORY_INT_FILTERS_DATA)
            viewModel.getFilterProducts("Bearer $TOKEN_USER", MAP_FILTERS_PRODUCTS)
            startState()
            isStart()
        }else{
            MAP_FILTERS_PRODUCTS.clear()
            viewModel.getCategoryIDEnd("Bearer $TOKEN_USER", FILTER_INT_ALL[FILTER_INT_ALL.lastIndex])
            MAP_FILTERS_PRODUCTS["category"] = FILTER_INT_ALL[FILTER_INT_ALL.lastIndex].toString()
            viewModel.getFilterProductsEND("Bearer $TOKEN_USER", MAP_FILTERS_PRODUCTS)
            endState()
            isEnd()
        }






        view.swipeRefreshLayout.setOnRefreshListener {
            onResume()
            adapter.notifyDataSetChanged()
            view.swipeRefreshLayout.isRefreshing = false
        }



        view.moveToBackAdapter.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        view.hhBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        view.imgHomeFilters.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                STATE_INT_FILTERS = false

                if ( FILTER_INT_ALL.size != 1){
                    FILTER_INT_ALL.removeAt(FILTER_INT_ALL.lastIndex)
                }

                findNavController().popBackStack()
            }
        })
        return view.root
    }




    @SuppressLint("NotifyDataSetChanged")
    fun isStart() {
        try {
            val arInData = ArrayList<Data>()
            viewModel.myFilterProducts.observe(viewLifecycleOwner) { list ->
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

    @SuppressLint("NotifyDataSetChanged")
    fun isEnd() {
        try {
            val arInData = ArrayList<Data>()
            viewModel.myFilterProductsEND.observe(viewLifecycleOwner) { list ->
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
        adapterCF = CategoryFiltersAdapterPlus(object : IClickListnearHomeFilterCategory {

            override fun clickListener(id: Int, name: String) {
                CATEGORY_INT_FILTERS_DATA = id
                FILTER_INT_ALL.add(id)
                MAP_FILTERS_PRODUCTS.clear()
                MAP_FILTERS_PRODUCTS["category"] = id.toString()
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeFragmentPlusTradeIn_self)
            }

        })

        recyclerViewCF.adapter = adapterCF
        recyclerViewCF.setHasFixedSize(true)
    }

    private fun startState(){
        viewModel.myGetCategoryID.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding?.loaderfilter?.visibility = View.GONE
                binding?.rvCategoryPage?.visibility = View.VISIBLE
                list.body()?.data?.children.let {
                    if (it != null) {
                        adapterCF.setList(it)
                    }
                }

            } else {
                binding?.loaderfilter?.visibility = View.GONE
                binding?.rvCategoryPage?.visibility = View.VISIBLE
                uToast(requireContext(), "Error Server")
            }


        }

    }

    private fun endState(){
        viewModel.myGetCategoryEnd.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding?.loaderfilter?.visibility = View.GONE
                binding?.rvCategoryPage?.visibility = View.VISIBLE
                list.body()?.data?.children.let {
                    if (it != null) {
                        adapterCF.setList(it)
                    }
                }

            } else {
                binding?.loaderfilter?.visibility = View.GONE
                binding?.rvCategoryPage?.visibility = View.VISIBLE
                uToast(requireContext(), "Error Server")
            }


        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.toAction?.homeBack?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }

}