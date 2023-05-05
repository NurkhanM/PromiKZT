package product.promikz.screens.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import product.promikz.R
import com.google.android.material.tabs.TabLayoutMediator
import product.promikz.databinding.FragmentTwoAdvertBinding

class TwoAdvertFragment : Fragment() {

    private var _binding: FragmentTwoAdvertBinding? = null
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
        _binding = FragmentTwoAdvertBinding.inflate(inflater, container, false)
        val view = binding
        view.viewPager2.adapter = ViewPagAdapter2(ctx as FragmentActivity)
        view.viewPager2.isUserInputEnabled = false

        TabLayoutMediator(view.tabLayout2, view.viewPager2) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.setIcon(R.drawable.all_machine)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_baseline_compare_arrows_24)
                }
            }
        }.attach()
        view.viewPager2.isSelected

        return view.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}