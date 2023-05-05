package product.promikz.screens.auth.fastRegister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.phone.SmsRetriever
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import product.promikz.AppConstants.FAST_PHONE
import product.promikz.R
import product.promikz.databinding.ActivityAuthBinding
import product.promikz.databinding.FragmentFastRegisterBinding
import product.promikz.screens.auth.AuthActivity
import product.promikz.viewModels.ProfileViewModel

class FastRegisterFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private var _binding: FragmentFastRegisterBinding? = null
    private val binding get() = _binding!!
    private var activityBinding: ActivityAuthBinding? = null




    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentFastRegisterBinding.inflate(inflater, container, false)


        viewModel.fastRegister(rb(FAST_PHONE), rb("1"))
        startSmartUserConsent()


        viewModel.myFastRegister.observe(viewLifecycleOwner) { response ->

            if (response.isSuccessful) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_nav_fast_register_to_fastCheckPhoneCodeFragment)
            }else{
                activity?.onBackPressed()
            }

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? AuthActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.bottomNavigation?.visibility = View.GONE
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null)
    }

    fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }
}