package product.promikz.screens.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.AppConstants
import product.promikz.MyUtils
import product.promikz.databinding.FragmentUserBinding
import product.promikz.screens.auth.confirm.StateConfirmActivity

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    lateinit var dialog: Dialog
    private var isMenu: Boolean = false

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding
        dialog = Dialog(requireContext())
        view.userChange.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_userFragment_to_userEditFragment)
        }

        view.itemMenu.setOnClickListener {
            if (!isMenu) {
                view.showDopMenu.visibility = View.VISIBLE
                isMenu = true
            } else {
                view.showDopMenu.visibility = View.GONE
                isMenu = false
            }
        }

        view.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        view.userVerifyButton.setOnClickListener {

            val intent = Intent(requireActivity(), StateConfirmActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )

        }




        view.userBtnChange.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_userFragment_to_userEditFragment)
        }

        view.nextDeleteAccount.setOnClickListener {
            view.showDopMenu.visibility = View.GONE
            isMenu = false
            alertDialogCancel()
        }

        return view.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        viewModel.getUser("Bearer $TOKEN_USER")
        viewModel.myUser.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                AppConstants.VERIFY_USER_PHONE = list.body()?.data?.phone_verified_at != null
                AppConstants.VERIFY_USER_EMAIL = list.body()?.data?.email_verified_at != null

                AppConstants.USER_TYPE = list.body()?.data?.type.toString()
                AppConstants.USER_ID = list.body()?.data?.id!!
                AppConstants.totalNotification = list.body()?.data?.unreadNotifications!!
                if (list.body()?.data?.shop != null) {
                    AppConstants.ID_SHOP_MY = list.body()?.data?.shop!!.id
                    AppConstants.ID_SHOP_USER = list.body()?.data?.shop!!.id
                    AppConstants.shopInfo = true
                }
                AppConstants.userIDChat = list.body()?.data?.id


                binding.userName.text = list.body()?.data?.name
                binding.userEmail.text = list.body()?.data?.email
                binding.userStatus.text = list.body()?.data?.status
                binding.userDate.text = list.body()?.data?.created_at
                binding.userType.text = list.body()?.data?.type?.let { getStatusUser(it) }
                if (list.body()?.data?.type != "0"){
                    binding.isStateType.visibility = View.GONE
                }
                binding.userPhone.text = list.body()?.data?.phone
                binding.userCountry.text = list.body()?.data?.country?.name
                MyUtils.uGlide(requireContext(), binding.userImage, list.body()?.data?.img)
//                val navView = (activity as AppCompatActivity).nav_view
//                val hView: View = navView.getHeaderView(0)
//                val imageView: ImageView = hView.findViewById(R.id.image_circule_nav)
//                MyUtils.uGlide(requireContext(), imageView, list.body()?.data?.img)
            }
        }
    }

    private fun getStatusUser(status: String): String {
        if (status == "0") {
            return resources.getText(R.string.phys_face).toString()
        }
        return if (status == "1") {
            resources.getText(R.string.jur_face).toString()
        } else {
            resources.getText(R.string.educatin_institution).toString()
        }
    }

    private fun alertDialogCancel() {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_delete_user)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes)
        val buttonNO = dialog.findViewById<Button>(R.id.dialog_no)

        buttonNO.setOnClickListener {
            dialog.dismiss()
        }
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}