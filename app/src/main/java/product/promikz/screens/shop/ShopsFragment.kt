package product.promikz.screens.shop

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayoutMediator
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.R
import product.promikz.screens.create.newShop.ShopCreateActivity
import product.promikz.viewModels.HomeViewModel
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.VERIFY_USER_PHONE
import product.promikz.MyUtils
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentShopsBinding
import product.promikz.screens.profile.UserViewPageAdapter
import product.promikz.screens.refresh.RefreshActivity

class ShopsFragment : Fragment() {

    private lateinit var binding: FragmentShopsBinding

    private lateinit var viewModel: HomeViewModel
    private var ctx: Context? = null
    lateinit var dialog: Dialog
    private var isMenu: Boolean = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentShopsBinding.inflate(inflater, container, false)

        binding.userViewPager.adapter = UserViewPageAdapter(ctx as FragmentActivity)
        dialog = Dialog(requireContext())



        binding.userChange.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_shopsFragment_to_shopChangeFragment)
        }

        binding.itemMenu.setOnClickListener {
            if (!isMenu) {
                binding.showDopMenu.visibility = View.VISIBLE
                isMenu = true
            } else {
                binding.showDopMenu.visibility = View.GONE
                isMenu = false
            }
        }

        binding.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.clickUserBackCard2.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.userBtnChange.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_shopsFragment_to_shopChangeFragment)
        }


        binding.nextDeleteShop.setOnClickListener {
            binding.showDopMenu.visibility = View.GONE
            isMenu = false
            alertDialogCancel()
        }


        TabLayoutMediator(binding.userTabLayout, binding.userViewPager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getText(R.string.active)
                }
                1 -> {
                    tab.text = resources.getText(R.string.inactive)
                }
            }
        }.attach()



        viewModel.myDeleteShop.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                val intent = Intent(requireActivity(), RefreshActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                activity?.finish()
            } else {
                uToast(requireContext(), "Error Shops")
            }

        }

        return binding.root
    }

        override fun onResume() {
            super.onResume()

            if (ID_SHOP_USER == -1) {

                binding.scrcrolView.visibility = View.GONE
                binding.profileCons3.visibility = View.VISIBLE

                binding.buttonCreateShop.setOnClickListener {

                    if (VERIFY_USER_PHONE) {
                        val intent = Intent(requireActivity(), ShopCreateActivity::class.java)
                        startActivity(intent)
                    } else {
                        MyUtils.uGlide(requireContext(), binding.imageViewCat, R.drawable.gmail)
                        binding.textViewCat.text = resources.getString(R.string.gmail_create)
                        binding.buttonCreateShop.isClickable = false
                    }

                }

            } else {

                viewModel.getShops("Bearer $TOKEN_USER", ID_SHOP_USER)
                viewModel.myShopsModels.observe(viewLifecycleOwner) { list ->
                    if (list.isSuccessful) {
                        binding.shopName.text = list.body()?.data?.name
                        binding.shopEmail.text = list.body()?.data?.user?.email
                        binding.shopDescription.text = list.body()?.data?.description
                        binding.dateRegister.text = list.body()?.data?.created_at
                        MyUtils.uGlide(requireContext(), binding.shopImage, list.body()?.data?.img)
                    }
                }

                binding.scrcrolView.visibility = View.VISIBLE

                binding.profileCons3.visibility = View.GONE
            }

    }


        private fun alertDialogCancel() {

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.dialog_delete_shop)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes)
            val buttonNO = dialog.findViewById<Button>(R.id.dialog_no)

            buttonNO.setOnClickListener {
                dialog.dismiss()
            }
            buttonYES.setOnClickListener {
                viewModel.postDeleteShop("Bearer $TOKEN_USER", "delete", ID_SHOP_USER)
                dialog.dismiss()
            }

            dialog.show()

        }


    }