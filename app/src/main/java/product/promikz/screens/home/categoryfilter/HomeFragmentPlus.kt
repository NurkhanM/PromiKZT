package product.promikz.screens.home.categoryfilter

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
import product.promikz.AppConstants.CATEGORY_INT_SEARCH_DATA
import product.promikz.AppConstants.FILTER_INT_ALL
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS_BRANDS
import product.promikz.AppConstants.STATE_INT_FILTERS
import product.promikz.AppConstants.STATE_IS_BRAND
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentHomePlusBinding
import product.promikz.databinding.FragmentTwoAdvertBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory
import product.promikz.models.products.index.Data
import product.promikz.screens.home.TovarAdapterHome
import product.promikz.screens.search.products.SSortActivity


class HomeFragmentPlus : Fragment() {

    private var _binding: FragmentHomePlusBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null
    private var activityBinding2: FragmentTwoAdvertBinding? = null


    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCF: RecyclerView
    lateinit var adapter: TovarAdapterHome
    private lateinit var adapterCF: CategoryFiltersAdapterPlus
    private lateinit var viewModel: HomeViewModel

    private var isSubscrip = false

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomePlusBinding.inflate(inflater, container, false)
        val view = binding

        categoryFilterMed()

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
                    } catch (e: ActivityNotFoundException) {
                        uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

        })
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        if (STATE_INT_FILTERS) {
            viewModel.getCategoryID("Bearer $TOKEN_USER", CATEGORY_INT_FILTERS_DATA)
            viewModel.getFilterProducts("Bearer $TOKEN_USER", MAP_FILTERS_PRODUCTS)
            startState()
            isStart()
        } else {
            MAP_FILTERS_PRODUCTS.clear()
            viewModel.getCategoryIDEnd(
                "Bearer $TOKEN_USER",
                FILTER_INT_ALL[FILTER_INT_ALL.lastIndex]
            )
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

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    STATE_INT_FILTERS = false

                    CATEGORY_INT_SEARCH_DATA = if (FILTER_INT_ALL.size != 1) {
                        FILTER_INT_ALL.removeAt(FILTER_INT_ALL.lastIndex)
                        FILTER_INT_ALL[FILTER_INT_ALL.lastIndex]
                    } else {
                        0
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
                        if (item.type == 0 || item.type == 3) {
                            arInData.add(item)
                        }
                    }
                    if (arInData.isEmpty()){
                        binding.consProducNull.visibility = View.VISIBLE
                    }else{
                        binding.consProducNull.visibility = View.GONE
                    }
                    adapter.setList(arInData)
                    adapter.notifyDataSetChanged()
                }
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding.dopText.visibility = View.VISIBLE
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
                        if (item.type == 0 || item.type == 3) {
                            arInData.add(item)
                        }
                    }
                    if (arInData.isEmpty()){
                        binding.consProducNull.visibility = View.VISIBLE
                    }else{
                        binding.consProducNull.visibility = View.GONE
                    }
                    adapter.setList(arInData)
                    adapter.notifyDataSetChanged()
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding.dopText.visibility = View.VISIBLE
        }

    }


    private fun categoryFilterMed() {
        recyclerViewCF = binding.rvCategoryPage
        adapterCF = CategoryFiltersAdapterPlus(object : IClickListnearHomeFilterCategory {

            override fun clickListener(id: Int, name: String, boolean: Boolean) {

                if (boolean) {
                    STATE_IS_BRAND = false
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_homeFragmentPlus_to_homeFragmentPlusBrand)
                    MAP_FILTERS_PRODUCTS_BRANDS.clear()
                    MAP_FILTERS_PRODUCTS_BRANDS["category"] = id.toString()
                } else {
                    CATEGORY_INT_FILTERS_DATA = id
                    CATEGORY_INT_SEARCH_DATA = id
                    FILTER_INT_ALL.add(id)
                    MAP_FILTERS_PRODUCTS.clear()
                    MAP_FILTERS_PRODUCTS["category"] = id.toString()
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_homeFragmentPlus_self)

                }
            }

        })

        recyclerViewCF.adapter = adapterCF
        recyclerViewCF.setHasFixedSize(true)
    }

    private fun startState() {
        viewModel.myGetCategoryID.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                list.body()?.data?.children.let {
                    if (it != null) {
                        adapterCF.setList(it)
                    }
                }

                stateImageSubscriber(list?.body()?.data?.isSubscriber!!)
                binding.textCategory.text = list.body()?.data?.name
                binding.imgHomeFavorite.setOnClickListener {
                    isSubscrip = !isSubscrip
                    stateImageSubscriber(isSubscrip)
                    viewModel.subCategory("Bearer $TOKEN_USER", list.body()?.data?.id!!)
                }


            } else {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                uToast(requireContext(), "Error Server")
            }


        }

    }

    private fun endState() {
        viewModel.myGetCategoryEnd.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                list.body()?.data?.children.let {
                    if (it != null) {
                        adapterCF.setList(it)
                    }
                }
                stateImageSubscriber(list?.body()?.data?.isSubscriber!!)
                binding.textCategory.text = list.body()?.data?.name
                binding.imgHomeFavorite.setOnClickListener {
                    isSubscrip = !isSubscrip
                    stateImageSubscriber(isSubscrip)
                    viewModel.subCategory("Bearer $TOKEN_USER", list.body()?.data?.id!!)
                }

            } else {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                uToast(requireContext(), "Error Server")
            }


        }

    }

    private fun stateImageSubscriber(boolean: Boolean) {

        isSubscrip = if (boolean) {
            binding.imgHomeFavorite.setImageResource(R.drawable.ic_star2)
            true
        } else {
            binding.imgHomeFavorite.setImageResource(R.drawable.ic_star)
            false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.toAction?.homeBack?.visibility = View.GONE

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
        activityBinding2 = null
    }

}