package product.promikz.screens.root

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import product.promikz.*
import product.promikz.screens.hometwo.adapters.ViewPagAdapter
import com.google.android.material.tabs.TabLayoutMediator
import product.promikz.databinding.FragmentRootBinding


class RootFragment : Fragment() {

    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!


    private var ctx: Context? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        ctx = context
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentRootBinding.inflate(inflater, container, false)
        val view = binding


        view.viewPager.adapter = ViewPagAdapter(ctx as FragmentActivity)
        view.viewPager.isUserInputEnabled = false

        TabLayoutMediator(view.tabLayout, view.viewPager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.setIcon(R.drawable.ic_baseline_home_24)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_add)
                }
                2 -> {
                    tab.setIcon(R.drawable.ic_store)
                }
            }
        }.attach()


        // НЕ УДАЛЯЙ НУЖНЫЙ ВЕЩЬ

//        activity?.onBackPressedDispatcher?.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (view.view_pager.currentItem != 0) {
//                        view.view_pager.currentItem = 0
//                    } else {
//                        activity!!.finish()
//                    }
//
//                }
//            })




        return view.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}