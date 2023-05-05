package product.promikz.screens.auth.reset

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.CHECK_CODE_RESET
import product.promikz.MainActivity
import product.promikz.R
import product.promikz.MyUtils
import product.promikz.databinding.FragmentNewPasswordBinding
import product.promikz.viewModels.HomeViewModel

class NewPasswordFragment : Fragment() {

    lateinit var viewModels: HomeViewModel

    private lateinit var preferencesIdShopUser: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences

    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        val view = binding

        preferencesTOKEN = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        preferencesIdShopUser = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        viewModels.myResetPassword.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                AppConstants.VERIFY_USER_PHONE = list.body()?.user?.phone_verified_at != null
                AppConstants.VERIFY_USER_EMAIL = list.body()?.user?.email_verified_at != null

                AppConstants.USER_TYPE = list.body()?.user?.type.toString()
                AppConstants.USER_ID = list.body()?.user?.id!!
                AppConstants.totalNotification = list.body()?.user?.unreadNotifications!!
                if (list.body()?.user?.shop != null) {
                    AppConstants.ID_SHOP_MY = list.body()?.user?.shop!!.id
                    AppConstants.ID_SHOP_USER = list.body()?.user?.shop!!.id
                    AppConstants.shopInfo = true
                }
                AppConstants.TOKEN_USER = list.body()?.token.toString()
                AppConstants.userIDChat = list.body()?.user?.id

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                activity?.finish()
                MyUtils.uToast(requireContext(), "Вы успешно изменили пароль")
            } else {

                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                val jsonObjError = JSONObject(jsonObj.getString("errors"))


                for (name in jsonObjError.keys()){

                    val nameArray = jsonObjError.getJSONArray(name)

                    for (i in 0 until nameArray.length()){
                        MyUtils.uToast(requireContext(), nameArray.getString(i))
                    }

                }

            }

            preferencesIdShopUser.edit().putInt(
                AppConstants.KEY_ID_SHOP_MY,
                AppConstants.ID_SHOP_MY
            ).apply()
            preferencesTOKEN.edit().putString(AppConstants.KEY_TOKEN, AppConstants.TOKEN_USER)
                .apply()
        }


        view.btnSave.setOnClickListener {
            if (view.editNewPassword.text?.isNotEmpty() == true &&
                view.editNewPassword2.text?.isNotEmpty() == true
            ) {
                viewModels.resetPassword(
                    CHECK_CODE_RESET, view.editNewPassword.text.toString(),
                    view.editNewPassword2.text.toString()
                )
            } else {
                MyUtils.uToast(requireContext(), "Пустое поле")
            }
        }


        view.editNewPassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                view.textInputLayout.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                view.textInputLayout.error = null
            }
        }

        view.editNewPassword2.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                view.textInputLayout2.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                view.textInputLayout2.error = null
            }
        }



        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}