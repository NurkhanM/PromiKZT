package product.promikz.screens.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import product.promikz.screens.profile.active.ProfileActiveFragment
import product.promikz.screens.profile.de_active.DeActiveFragment

class UserViewPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileActiveFragment()
            else -> DeActiveFragment()
        }
    }


}