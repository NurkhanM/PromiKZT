package product.promikz.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import product.promikz.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.shopInfo
import product.promikz.screens.pageErrorNetworks.ServerErrorActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import product.promikz.AppConstants.VERIFY_USER_PHONE
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentProfileBinding
import product.promikz.screens.create.newShop.ShopCreateActivity
import product.promikz.viewModels.HomeViewModel
import product.promikz.viewModels.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null

    private var ctx: Context? = null
    private lateinit var vieww: View

    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelHome: HomeViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }


    @SuppressLint("DetachAndAttachSameFragment")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModelHome = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding
        view.userViewPager.adapter = UserViewPageAdapter(ctx as FragmentActivity)

        if (VERIFY_USER_PHONE && shopInfo) {
            view.profileCons2.visibility = View.VISIBLE
            view.profileCons3.visibility = View.GONE
            view.progressProfile.visibility = View.GONE
        } else {
            view.profileCons2.visibility = View.GONE
            view.profileCons3.visibility = View.VISIBLE
            view.progressProfile.visibility = View.GONE
        }


        view.buttonCreateShop.setOnClickListener {
            viewModelHome.getUser("Bearer $TOKEN_USER")
            viewModelHome.myUser.observe(viewLifecycleOwner) { list ->

                VERIFY_USER_PHONE = list.body()?.data?.phone_verified_at != null
            }
            if (VERIFY_USER_PHONE) {
                val intent = Intent(requireActivity(), ShopCreateActivity::class.java)
                startActivity(intent)
            } else {
                Glide.with(requireContext()).load(R.drawable.gmail)
                    .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                    .centerCrop().into(view.imageViewCat)
                view.textViewCat.text = resources.getString(R.string.gmail_create)
                view.buttonCreateShop.isClickable = false
                try {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        view.buttonCreateShop.isClickable = true
                        Glide.with(requireContext()).load(R.drawable.create_shop_img)
                            .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                            .centerCrop().into(view.imageViewCat)
                        view.textViewCat.text = resources.getString(R.string.create_shop_text)
                    }
                } catch (e: IllegalStateException) {
                    Toast.makeText(
                        requireContext(),
                        "$e ${resources.getText(R.string.oops_mistake)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }




        TabLayoutMediator(view.userTabLayout, view.userViewPager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getText(R.string.active)
                }
                1 -> {
                    tab.text = resources.getText(R.string.inactive)
                }
            }
        }.attach()




        return view.root
    }

    private fun ref() {
        binding.profileCons2?.visibility = View.GONE
        binding.profileCons3?.visibility = View.GONE
        binding.progressProfile?.visibility = View.VISIBLE
        viewModelHome.getUser("Bearer $TOKEN_USER")
        viewModelHome.myUser.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                if (response.body()?.data?.shop?.id != null) {
                    binding.profileCons2?.visibility = View.VISIBLE
                    binding.profileCons3?.visibility = View.GONE
                    binding.progressProfile?.visibility = View.GONE
                } else {
                    binding.profileCons2?.visibility = View.GONE
                    binding.profileCons3?.visibility = View.VISIBLE
                    binding.progressProfile?.visibility = View.GONE
                }
            } else {
                val intent = Intent(requireActivity(), ServerErrorActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                (activity as AppCompatActivity).finish()
            }
        }
    }

    //no minus
    override fun onResume() {
        super.onResume()
        ref()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        binding.profileDrawer.setOnClickListener {
            activityBinding?.layoutDrawer?.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }

}