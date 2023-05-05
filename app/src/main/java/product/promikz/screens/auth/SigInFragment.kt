package product.promikz.screens.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.ID_SHOP_MY
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.KEY_ID_SHOP_MY
import product.promikz.AppConstants.KEY_TOKEN
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_ID
import product.promikz.AppConstants.USER_TYPE
import product.promikz.AppConstants.VERIFY_USER_EMAIL
import product.promikz.AppConstants.VERIFY_USER_PHONE
import product.promikz.AppConstants.shopInfo
import product.promikz.AppConstants.totalNotification
import product.promikz.AppConstants.userIDChat
import product.promikz.MainActivity
import product.promikz.R
import product.promikz.databinding.ActivityAuthBinding
import product.promikz.databinding.FragmentSigInBinding
import product.promikz.viewModels.ProfileViewModel

class SigInFragment : Fragment() {


    private val profileAuth = HashMap<String, String>()

    private lateinit var viewModel: ProfileViewModel

    private lateinit var preferencesIdShopUser: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences

    private var _binding: FragmentSigInBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityAuthBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentSigInBinding.inflate(inflater, container, false)
        val view = binding

        preferencesTOKEN = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        preferencesIdShopUser = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )


        view.button2.setOnClickListener {

            view.loaderButton.visibility = View.VISIBLE
            view.button2.visibility = View.GONE
//            view.buttonNewRegisterUser.visibility = View.GONE

            if (view.buttonProfileLogin.length() != 0 && view.buttonProfilePassword.length() != 0) {

                profileAuth["email"] = view.buttonProfileLogin.text.toString().trim()
                profileAuth["password"] = view.buttonProfilePassword.text.toString().trim()
                if (view.remember.isChecked) {
                    profileAuth["remember"] = "1"
                } else {
                    profileAuth["remember"] = "0"
                }

                viewModel.setPushPost(profileAuth)

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.null_login_or_password),
                    Toast.LENGTH_SHORT
                ).show()
                view.loaderButton.visibility = View.GONE
                view.button2.visibility = View.VISIBLE
//                view.buttonNewRegisterUser.visibility = View.VISIBLE
            }
        }


        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        fun String.isValidPhone() = isNotEmpty() && android.util.Patterns.PHONE.matcher(this).matches()

        view.buttonProfileLogin.doOnTextChanged { _, _, _, count ->
            if (count > 3) {
                if (view.buttonProfileLogin.text.toString().isValidEmail() || view.buttonProfileLogin.text.toString().isValidPhone()) {
                    view.textInputLayout2.helperText =
                        resources.getText(R.string.email_or_phone)
                } else {
                    view.textInputLayout2.error = resources.getText(R.string.enter_email_correctly)
                }
            } else {
                view.textInputLayout2.helperText = resources.getText(R.string.enter_postal_address)
            }
        }



//        view.buttonNewRegisterUser.setOnClickListener {
//            try {
//                Navigation.findNavController(view)
//                    .navigate(R.id.action_sigInFragment_to_register)
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Упс!!!", Toast.LENGTH_SHORT).show()
//            }
//        }


        view.requestPassword.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_sigInFragment_to_resetFragment)

        }


        viewModel.getPushPost.observe(viewLifecycleOwner) { response ->



            if (response.isSuccessful) {
                VERIFY_USER_PHONE = response.body()?.user?.phone_verified_at != null
                VERIFY_USER_EMAIL = response.body()?.user?.email_verified_at != null

                USER_TYPE = response.body()?.user?.type.toString()
                USER_ID = response.body()?.user?.id!!
                totalNotification = response.body()?.user?.unreadNotifications!!
                if (response.body()?.user?.shop != null) {
                    ID_SHOP_MY = response.body()?.user?.shop!!.id
                    ID_SHOP_USER = response.body()?.user?.shop!!.id
                    shopInfo = true
                }
                TOKEN_USER = response.body()?.token.toString()
                userIDChat = response.body()?.user?.id

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                activity?.finish()

            } else {

                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                Toast.makeText(requireContext(), jsonObj.getString("message").toString(), Toast.LENGTH_SHORT).show()
                view.loaderButton.visibility = View.GONE
                view.button2.visibility = View.VISIBLE
//                view.buttonNewRegisterUser.visibility = View.VISIBLE
            }

            preferencesIdShopUser.edit().putInt(KEY_ID_SHOP_MY, ID_SHOP_MY).apply()
            preferencesTOKEN.edit().putString(KEY_TOKEN, TOKEN_USER).apply()
        }


//        view.buttonProfileLogin.doOnTextChanged { text, _, _, _ ->
//            if (text!!.isNotEmpty()) {
//                view.button2.backgroundTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(requireContext(), R.color.fon1)
//                )
//                view.button2.isEnabled = true
//            } else if (text.length <= 18) {
//                view.button2.backgroundTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(requireContext(), R.color.white3)
//                )
//                view.button2.isEnabled = false
//            }
//        }

        view.buttonProfilePassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()){
                if (view.buttonProfileLogin.text?.isNotEmpty() == true){
                    view.button2.isEnabled = true
                    view.button2.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.fon1)
                    )
                } else{
                    view.button2.isEnabled = false
                    view.button2.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.white3)
                    )
                }
                if (text.length < 8) {
                    view.textInputLayout3.error = resources.getText(R.string.minimum_8_password)
                } else if (text.length <= 18) {
                    view.textInputLayout3.error = null
                }
            } else {
                view.button2.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.white3)
                )
                view.button2.isEnabled = false
            }



        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? AuthActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.bottomNavigation?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }


}