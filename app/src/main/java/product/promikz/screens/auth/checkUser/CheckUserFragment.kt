package product.promikz.screens.auth.checkUser

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import product.promikz.AppConstants.FAST_PHONE
import product.promikz.R
import product.promikz.databinding.ActivityAuthBinding
import product.promikz.databinding.FragmentCheckUserBinding
import product.promikz.screens.auth.AuthActivity
import product.promikz.viewModels.HomeViewModel

class CheckUserFragment : Fragment() {

    private var _binding: FragmentCheckUserBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityAuthBinding? = null


    private lateinit var animation: Animation



    private lateinit var viewModel: HomeViewModel
    lateinit var dialog: Dialog
    private var sizeEditNumber = -1

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCheckUserBinding.inflate(inflater, container, false)

        dialog = Dialog(requireContext())

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right_auth)
        animation.fillAfter = true
        animation.duration = 500


        binding.floatingActionButton.setOnClickListener {
            if (binding.textview.visibility == View.GONE) {
                binding.textview.visibility = View.VISIBLE
                binding.textview.startAnimation(animation)
            } else {
                binding.textview.visibility = View.GONE
                binding.textview.clearAnimation()
            }
        }






        binding.nextFastSignIn.setOnClickListener {

            binding.resetWorkError.visibility = View.VISIBLE
            binding.resetLin.visibility = View.INVISIBLE
            if (binding.authEditPhone.text.toString().isNotEmpty()) {
                val number = "${binding.authEditPhone.selectedCountryDialCode}${binding.authEditPhone.text.toString()}"
                viewModel.fastPhone(number.trim())
                FAST_PHONE = number.trim()
                sizeEditNumber = number.length

            } else {
                Toast.makeText(requireContext(), "Заполните поле!", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    binding.resetWorkError.visibility = View.GONE
                    binding.resetLin.visibility = View.VISIBLE
                }
            }
        }

        viewModel.myFastPhone.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding.resetWorkError.visibility = View.GONE
                binding.resetLin.visibility = View.VISIBLE
                if (list.body()?.status == true) {
                    binding.authEditPhone.setText("")
                    binding.nextNewFastAccount.visibility = View.GONE
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_nav_fast_phone_to_enteredPasswordFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Такого аккаунта не существует",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.nextNewFastAccount.text = "${resources.getString(R.string.register_new_number)} $FAST_PHONE"
                    binding.nextNewFastAccount.visibility = View.VISIBLE
                }

            } else {

                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                val jsonObj2 = JSONObject(jsonObj.getString("errors").toString())
                val strString = jsonObj2.getString("phone").toString().replace("[\"", "").replace(".\"]", "")

                binding.resetWorkError.visibility = View.GONE
                binding.resetLin.visibility = View.VISIBLE
                Toast.makeText(requireContext (), strString, Toast.LENGTH_SHORT).show()

            }


        }



        binding.nextNewFastAccount.setOnClickListener {
            val number = "${binding.authEditPhone.selectedCountryDialCode}${binding.authEditPhone.text.toString()}"
            FAST_PHONE = number.trim()
            binding.nextNewFastAccount.visibility = View.GONE
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_checkUserFragment_to_nav_fast_register)
        }


        binding.authEditPhone.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.nextFastSignIn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fon1)
                )
                binding.nextFastSignIn.isEnabled = true
                if (text.length < sizeEditNumber){
                    binding.nextNewFastAccount.visibility = View.GONE
                }

            }else{
                binding.nextFastSignIn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.white3)
                )
                binding.nextNewFastAccount.visibility = View.GONE
                binding.nextFastSignIn.isEnabled = false
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