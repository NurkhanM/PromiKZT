package product.promikz.screens.specialist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.specialist.info.InfoSpesialistActivity
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.AppConstants
import product.promikz.databinding.FragmentSpecialistBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.databinding.ItemSpecialistModelsBinding
import product.promikz.inteface.IClickListnearHomeSpecialist
import product.promikz.screens.specialist.filter.FilterSpecialistActivity

class SpecialistFragment : Fragment() {
    private var _binding: FragmentSpecialistBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: SpecialistAdapter
    private lateinit var viewModel: HomeViewModel


    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentSpecialistBinding.inflate(inflater, container, false)
        val view = binding

        recyclerView = view.rvSpecialist
        val space = 30
        val includeEdge = true
        recyclerView.addItemDecoration(SpaceItemDecoration(space, includeEdge))
        adapter = SpecialistAdapter(object : IClickListnearHomeSpecialist {

            override fun clickListener(
                baseID: Int,
                viewAdapter: ItemSpecialistModelsBinding,
                isLike: Boolean,
                position: Int
            ) {
                val intent = Intent(requireActivity(), InfoSpesialistActivity::class.java)
                intent.putExtra("spec", baseID)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

            }

        })
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        view.speRefreshLayout.setOnRefreshListener {
            onResume()
            adapter.notifyDataSetChanged()
            view.speRefreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    view.speToBackAdapter.visibility = View.VISIBLE
                } else {
                    view.speToBackAdapter.visibility = View.GONE
                }
            }

        })

        view.speToBackAdapter.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        view.speBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        view.menuSpecialistInfo.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_specialistFragment_to_meSpecialistFragment)
        }

        view.imgSpecialistFilters.setOnClickListener {
            val intent = Intent(requireActivity(), FilterSpecialistActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }

        return view.root
    }


    override fun onResume() {
        super.onResume()
        viewModel.getSpecialistPage("Bearer ${AppConstants.TOKEN_USER}")
        viewModel.mySpecialistPage.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                list.body()?.data.let { adapter.setList(it!!) }
            } else {
                //todo
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}