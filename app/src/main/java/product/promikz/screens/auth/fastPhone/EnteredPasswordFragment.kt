package product.promikz.screens.auth.fastPhone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.MainActivity
import product.promikz.R
import product.promikz.MyUtils
import product.promikz.databinding.FragmentEnteredPasswordBinding
import product.promikz.viewModels.ProfileViewModel

class EnteredPasswordFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private val profileAuth = HashMap<String, String>()

    private lateinit var preferencesIdShopUser: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences

    private var _binding: FragmentEnteredPasswordBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_entered_password, container, false)
        _binding = FragmentEnteredPasswordBinding.inflate(inflater, container, false)

        preferencesTOKEN = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        preferencesIdShopUser = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        binding.btnConfirm.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            binding.btnConfirm.visibility = View.GONE
            if (binding.buttonProfilePassword.text?.isNotEmpty() == true) {

                profileAuth["email"] = AppConstants.FAST_PHONE
                profileAuth["password"] = binding.buttonProfilePassword.text.toString()
                profileAuth["remember"] = "0"
                viewModel.setPushPost(profileAuth)

            } else {
                MyUtils.uToast(requireContext(), "Пустое поле")
            }
        }

        viewModel.getPushPost.observe(viewLifecycleOwner) { response ->


            if (response.isSuccessful) {
                AppConstants.VERIFY_USER_PHONE = response.body()?.user?.phone_verified_at != null
                AppConstants.VERIFY_USER_EMAIL = response.body()?.user?.email_verified_at != null

                AppConstants.USER_TYPE = response.body()?.user?.type.toString()
                AppConstants.USER_ID = response.body()?.user?.id!!
                AppConstants.totalNotification = response.body()?.user?.unreadNotifications!!
                if (response.body()?.user?.shop != null) {
                    AppConstants.ID_SHOP_MY = response.body()?.user?.shop!!.id
                    AppConstants.ID_SHOP_USER = response.body()?.user?.shop!!.id
                    AppConstants.shopInfo = true
                }
                AppConstants.TOKEN_USER = response.body()?.token.toString()
                AppConstants.userIDChat = response.body()?.user?.id

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                activity?.finish()


            } else {

                binding.progress.visibility = View.GONE
                binding.btnConfirm.visibility = View.VISIBLE

                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                Toast.makeText(
                    requireContext(),
                    jsonObj.getString("message").toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            preferencesIdShopUser.edit().putInt(
                AppConstants.KEY_ID_SHOP_MY,
                AppConstants.ID_SHOP_MY
            ).apply()
            preferencesTOKEN.edit().putString(AppConstants.KEY_TOKEN, AppConstants.TOKEN_USER)
                .apply()
        }


        binding.requestPassword.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_enteredPasswordFragment_to_resetFragment)


        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_enteredPasswordFragment_to_nav_fast_phone)
                }
            })

        binding.sBackCard.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_enteredPasswordFragment_to_nav_fast_phone)
        }


        binding.buttonProfilePassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fon1)
                )
                binding.btnConfirm.isEnabled = true
            }else {
                binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.white3)
                )
                binding.btnConfirm.isEnabled = false
            }
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}