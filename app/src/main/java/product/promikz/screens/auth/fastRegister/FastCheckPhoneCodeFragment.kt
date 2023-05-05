package product.promikz.screens.auth.fastRegister

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.MainActivity
import product.promikz.R
import product.promikz.MyUtils
import product.promikz.databinding.FragmentFastCheckPhoneCodeBinding
import product.promikz.viewModels.ProfileViewModel
import java.util.regex.Pattern

class FastCheckPhoneCodeFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private val profileAuth = HashMap<String, String>()

    private lateinit var preferencesIdShopUser: SharedPreferences
    private lateinit var preferencesTOKEN: SharedPreferences

    private val reqConsert = 200
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    private var _binding: FragmentFastCheckPhoneCodeBinding? = null
    private val binding get() = _binding!!




    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentFastCheckPhoneCodeBinding.inflate(inflater, container, false)
        val view = binding

        preferencesTOKEN = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        preferencesIdShopUser = (activity as AppCompatActivity).getSharedPreferences(
            AppConstants.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )



        startSmartUserConsent()

        binding.btnConfirm.setOnClickListener {
            if (binding.authEditCode.text?.isNotEmpty() == true) {

                profileAuth["email"] = AppConstants.FAST_PHONE
                profileAuth["password"] = view.authEditCode.text.toString()
                profileAuth["remember"] = "0"
                profileAuth["type"] = "code"
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


        view.sBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return view.root
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == reqConsert) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPatter = Pattern.compile("(|^)\\d{6}")
        val matcher = otpPatter.matcher(message.toString())
        if (matcher.find()) {
            binding.authEditCode.setText(matcher.group(0))
        }
    }

    private fun registerBroadcastReceiver() {

        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                @Suppress("DEPRECATION")
                override fun onSuccess(messageIntent: Intent?) {
                    startActivityForResult(messageIntent, reqConsert)
                }

                override fun onFailure() {
                    MyUtils.uToast(requireContext(), "onFailure")
                }

            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        activity?.registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}