package product.promikz.screens.hometwo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import product.promikz.screens.create.FirstCreateFragment
import product.promikz.screens.hometwo.HomeTwoFragment
import product.promikz.screens.profile.ProfileFragment

class ViewPagAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeTwoFragment()
            1 -> FirstCreateFragment()
            else -> ProfileFragment()
        }
    }


}