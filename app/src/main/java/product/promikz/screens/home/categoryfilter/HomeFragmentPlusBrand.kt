package product.promikz.screens.home.categoryfilter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import product.promikz.*
import product.promikz.AppConstants.BRAND_INT_FILTERS_DATA
import product.promikz.AppConstants.CATEGORY_INT_FILTERS_DATA
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.inteface.IClickListnearHomeStory
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import com.google.android.gms.common.api.ApiException
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentHomePlusBrandBinding
import product.promikz.databinding.FragmentTwoAdvertBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.screens.home.TovarAdapterHome
import product.promikz.screens.search.products.SSortActivity


class HomeFragmentPlusBrand : Fragment() {

    private var _binding: FragmentHomePlusBrandBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null
    private var activityBinding2: FragmentTwoAdvertBinding? = null

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCF: RecyclerView
    lateinit var adapter: TovarAdapterHome
    private lateinit var adapterBF: BrandFiltersAdapter
    private lateinit var viewModel: HomeViewModel


    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomePlusBrandBinding.inflate(inflater, container, false)
        val view = binding
        brandFilterMed()
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
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
                        startActivity(browserIntent)
                    } catch (e: ActivityNotFoundException){
                        uToast(requireContext(), "Error activity")
                    }

                }
            }

        })

        stateCF()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)






        view.swipeRefreshLayout.setOnRefreshListener {
            onResume()
            adapter.notifyDataSetChanged()
            view.swipeRefreshLayout.isRefreshing = false
        }


        view.moveToBackAdapter.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        view.hhBackCard.setOnClickListener {
            BRAND_INT_FILTERS_DATA = 0
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

        return view.root
    }

    private fun stateCF() {
        viewModel.getCategoryID("Bearer $TOKEN_USER", CATEGORY_INT_FILTERS_DATA)
        viewModel.myGetCategoryID.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                if (list.isSuccessful) {
                    binding.loaderfilter.visibility = View.GONE
                    binding.rvCategoryPage.visibility = View.VISIBLE
                    list.body()?.data?.brands.let {
                        adapterBF.setList(it!!)
                    }
                }

            } else {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                uToast(requireContext(), "Error Server Brand")
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            viewModel.getFilterProducts("Bearer $TOKEN_USER", MAP_FILTERS_PRODUCTS)
            viewModel.myFilterProducts.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful) {
                    recyclerView.removeAllViewsInLayout()
                    list.body()?.data?.let { adapter.setList(it) }
                    adapter.notifyDataSetChanged()
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding?.dopText?.visibility = View.VISIBLE
        }

    }

    private fun brandFilterMed() {
        recyclerViewCF = binding.rvCategoryPage
        adapterBF = BrandFiltersAdapter(object : IClickListnearHomeStory {
            override fun clickListener(pos: Int) {
                Navigation.findNavController(binding.root).navigate(R.id.action_homeFragmentPlusBrand_self)
            }

        })

        recyclerViewCF.adapter = adapterBF
        recyclerViewCF.setHasFixedSize(true)
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