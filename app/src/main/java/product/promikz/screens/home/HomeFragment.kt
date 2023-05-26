package product.promikz.screens.home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import product.promikz.*
import product.promikz.AppConstants.BRAND_INT_FILTERS_DATA
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.screens.home.categoryfilter.CategoryFiltersAdapter
import product.promikz.screens.update.UpdateActivity
import com.google.android.gms.common.api.ApiException
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants.CATEGORY_INT_FILTERS_DATA
import product.promikz.AppConstants.FILTERS_TYPE
import product.promikz.AppConstants.FILTER_INT_ALL
import product.promikz.AppConstants.MAP_FILTERS_PRODUCTS
import product.promikz.MyUtils.uLogD
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentHomeBinding
import product.promikz.databinding.FragmentTwoAdvertBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory
import product.promikz.screens.search.products.SSortActivity
import product.promikz.viewModels.HomeViewModel


class HomeFragment : Fragment() {

    private var token: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding not initialized.")

    private var activityBinding: ActivityMainBinding? = null
    private var activityBinding2: FragmentTwoAdvertBinding? = null

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCF: RecyclerView
    lateinit var adapter: TovarAdapterHome
    private lateinit var adapterCF: CategoryFiltersAdapter
    private val viewModel by viewModels<HomeViewModel>()


    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
                if (!isLike) {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                } else {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                }
                uLogD("token post Like == $TOKEN_USER")
                viewModel.postLike("Bearer $TOKEN_USER", baseID)

            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

                if (adver != 3) {
                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
                    intent.putExtra("hello", baseID)
                    startActivity(intent)
                    activity?.overridePendingTransition(
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

        stateCF()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        view.swipeRefreshLayout.setOnRefreshListener {
            onResume()
            view.swipeRefreshLayout.isRefreshing = false
        }



        view.moveToBackAdapter.setOnClickListener {
            uLogD("token getTovarPage == $TOKEN_USER")
            viewModel.getTovarPage("Bearer $TOKEN_USER")
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
            FILTERS_TYPE = "0"
        }

        return view.root
    }


    private fun stateCF() {

        viewModel.getCategoryIndex("Bearer $TOKEN_USER")
        viewModel.myCategoryIndex.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                binding.loaderfilter.visibility = View.GONE
                binding.rvCategoryPage.visibility = View.VISIBLE
                user.body()?.let {
                    adapterCF.setList(it.data)
                }
            } else {
                binding.loaderfilter.visibility = View.VISIBLE
                binding.rvCategoryPage.visibility = View.GONE
                uToast(requireContext(), "Error Server")
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            viewModel.getTovarPage("Bearer $TOKEN_USER")
            val arInData = ArrayList<product.promikz.models.products.index.Data>()
            viewModel.myTovarPage.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful) {
                    recyclerView.removeAllViewsInLayout()
                    arInData.clear()
                    list.body()?.data?.forEach { item ->
                        if (item.type == 0 || item.type == 3) {
                            arInData.add(item)
                        }
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
        adapterCF = CategoryFiltersAdapter(object : IClickListnearHomeFilterCategory {

            override fun clickListener(id: Int, name: String, boolean: Boolean) {
                CATEGORY_INT_FILTERS_DATA = id
                FILTER_INT_ALL.add(id)
                MAP_FILTERS_PRODUCTS.clear()
                MAP_FILTERS_PRODUCTS["category"] = id.toString()
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeTwoFragment_to_homeFragmentPlus)

            }

        })

        recyclerViewCF.adapter = adapterCF
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