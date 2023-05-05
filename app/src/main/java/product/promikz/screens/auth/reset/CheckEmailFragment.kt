package product.promikz.screens.auth.reset

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.phone.SmsRetriever
import product.promikz.AppConstants
import product.promikz.AppConstants.CHECK_CODE_RESET
import product.promikz.R
import product.promikz.MyUtils
import product.promikz.databinding.FragmentCheckEmailBinding
import product.promikz.screens.auth.fastRegister.SmsBroadcastReceiver
import product.promikz.viewModels.HomeViewModel
import java.util.regex.Pattern

class CheckEmailFragment : Fragment() {

    lateinit var viewModels: HomeViewModel

    private var _binding: FragmentCheckEmailBinding? = null
    private val binding get() = _binding!!

    private val reqConsert = 201
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null


    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCheckEmailBinding.inflate(inflater, container, false)
        val view = binding

        startSmartUserConsent()

        viewModels.myCodeCheckEmail.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                MyUtils.uToast(requireContext(), list.body()?.message)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_checkEmailFragment_to_newPasswordFragment)
            }else {
                view.progress.visibility = View.GONE
                view.btnConfirm.visibility = View.VISIBLE
                MyUtils.uToast(requireContext(), "Неправильный код")
            }


        }

        view.btnConfirm.setOnClickListener {
            if (view.authEditCode.text?.isNotEmpty() == true){
                CHECK_CODE_RESET = view.authEditCode.text.toString()
                view.progress.visibility = View.VISIBLE
                view.btnConfirm.visibility = View.GONE
                viewModels.codeCheckEmail("Bearer ${AppConstants.TOKEN_USER}", view.authEditCode.text.toString())
            }else {
                MyUtils.uToast(requireContext(), "Пустое поле")
            }
        }


        view.sBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return binding.root
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
            if (resultCode == Activity.RESULT_OK && data != null){
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPatter = Pattern.compile("(|^)\\d{6}")
        val matcher = otpPatter.matcher(message.toString())
        if (matcher.find()){
            binding?.authEditCode?.setText(matcher.group(0))
        }
    }

    private fun registerBroadcastReceiver(){

        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener{
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