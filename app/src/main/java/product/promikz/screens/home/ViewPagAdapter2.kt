package product.promikz.screens.home

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import product.promikz.screens.tradeIn.TradeInFragment

class ViewPagAdapter2(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    @SuppressLint("NotifyDataSetChanged")
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            else -> TradeInFragment()
        }
    }


}