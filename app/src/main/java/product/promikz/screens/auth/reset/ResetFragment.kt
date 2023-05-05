package product.promikz.screens.auth.reset

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentResetBinding

@Suppress("DEPRECATION")
class ResetFragment : Fragment() {

    private var _binding: FragmentResetBinding? = null
    private val binding get() = _binding!!




    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentResetBinding.inflate(inflater, container, false)
        val view = binding
        (activity as AppCompatActivity).supportActionBar?.hide()

        viewModel.myFargotEmail.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                Toast.makeText(requireContext(), "${list.body()?.message}", Toast.LENGTH_SHORT)
                    .show()
                view.authEditPhone.setText("")
                view.resetWorkError.visibility = View.GONE
                view.resetLin.visibility = View.VISIBLE
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_resetFragment_to_checkEmailFragment)
            } else {

                try {

                    // sdelat fun universal

                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))


                    for (name in jsonObjError.keys()){

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()){
                            uToast(requireContext(), nameArray.getString(i))
                        }

                    }

                    view.resetWorkError.visibility = View.GONE
                    view.resetLin.visibility = View.VISIBLE

                }catch (e: JSONException){
                    view.resetWorkError.visibility = View.GONE
                    view.resetLin.visibility = View.VISIBLE
                    uToast(requireContext(),"Error backend")
                }

            }


        }


        view.resetButton.setOnClickListener {

            view.resetWorkError.visibility = View.VISIBLE
            view.resetLin.visibility = View.INVISIBLE

            if (view.authEditPhone.text.toString().isNotEmpty()) {
                val number = "${view.authEditPhone.selectedCountryDialCode}${view.authEditPhone.text.toString()}"
                viewModel.fargotEmail(number.trim())
            } else {
                Toast.makeText(requireContext(), "Заполните поле!", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    view.resetWorkError.visibility = View.GONE
                    view.resetLin.visibility = View.VISIBLE
                }
            }
        }

//        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
//
//        view.resetEmail.doOnTextChanged { _, _, _, count ->
//            if (count > 3) {
//                if (view.resetEmail.text.toString().isValidEmail()) {
//                    view.emailLayout.helperText = "Введите почтовый адресс"
//                } else {
//                    view.emailLayout.error = "Введите email правильно"
//                }
//            } else {
//                view.emailLayout.helperText = "Введите почтовый адресс"
//            }
//        }

        view.resetBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}