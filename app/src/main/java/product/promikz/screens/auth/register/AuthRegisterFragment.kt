package product.promikz.screens.auth.register

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import product.promikz.AppConstants.PRIVACY_AGREEMENT
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MyUtils.uGlide
import product.promikz.R
import product.promikz.databinding.ActivityAuthBinding
import product.promikz.databinding.FragmentAuthRegisterBinding
import product.promikz.screens.auth.AuthActivity
import product.promikz.screens.auth.confirm.StateConfirmActivity
import product.promikz.viewModels.HomeViewModel
import product.promikz.viewModels.ProfileViewModel
import tech.hibk.searchablespinnerlibrary.SearchableItem
import java.io.File

class AuthRegisterFragment : Fragment() {
    private var filePart: MultipartBody.Part? = null
    private lateinit var viewModel: HomeViewModel
    private var posCountry: Int? = -2
    private var posType: Int? = -2
    lateinit var dialog: Dialog

    private var _binding: FragmentAuthRegisterBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityAuthBinding? = null

    @Suppress("DEPRECATION")
    @SuppressLint("QueryPermissionsNeeded", "Recycle")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val viewModelProfile = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentAuthRegisterBinding.inflate(inflater, container, false)
        val view = binding
        viewModel.getCountry()

        fun rb(value: String): RequestBody {
            return value.toRequestBody("text/plain".toMediaTypeOrNull())
        }


        stateEditText()
        dialog = Dialog(requireContext())
        view.authBtnPost.setOnClickListener {

            if (view.authEditName.length() != 0 && view.authEditEmail.length() != 0 &&
                view.authEditPassword.length() != 0 && view.authEditPassword2.length() != 0 &&
                posCountry != -2 && posType != -2 && view.authEditPhone.length() != 0

            ) {
                if (view.checkboxState.isChecked){
                    val number = "${view.authEditPhone.selectedCountryDialCode}${view.authEditPhone.text.toString()}"
                    viewModelProfile.pushRegist(
                        rb(view.authEditName.text.toString().trim()),
                        rb(view.authEditEmail.text.toString().trim()),
                        rb(view.authEditPassword.text.toString().trim()),
                        rb(view.authEditPassword2.text.toString().trim()),
                        rb(posCountry.toString()),
                        rb(posType.toString()),
                        rb(number.trim()),
                        filePart
                    )
                    view.profilteRegisterConstreite.visibility = View.GONE
                    view.profileProgress.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), resources.getText(R.string.confidentiality), Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), resources.getText(R.string.add_all_fields), Toast.LENGTH_SHORT).show()
            }


        }

        viewModelProfile.myRegisterList.observe(viewLifecycleOwner){ list ->
            if (list.isSuccessful){
                TOKEN_USER = list.body()?.token.toString()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    successRegister()
                    view.profileProgress.visibility = View.GONE
                }
            } else{
                view.profilteRegisterConstreite.visibility = View.VISIBLE
                view.profileProgress.visibility = View.GONE
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(),
                    jsonObj.getString("errors").toString().replace("{\"", "").replace("\"}", "").replace("\"],\"", "\n").replace("\":[\"", " -> ")
                )
            }
        }


        val getAction: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { ur ->

                try {
                    uGlide(requireContext(), binding.authImg, ur.data?.data)
                    lifecycleScope.launch {
                        filePartScopMetod(ur)
                    }


//                    val f = File.createTempFile("suffix", "prefix", requireContext().cacheDir)
//                    f.outputStream()
//                        .use {
//                            requireContext().contentResolver.openInputStream(ur.data?.data!!)
//                                ?.copyTo(it)
//                        }
//                    val requestBody = f.asRequestBody("image/*".toMediaTypeOrNull())
//                    filePart = MultipartBody.Part.createFormData("img", f.name, requestBody)

                } catch (e: Exception) {
                    e.printStackTrace()
                    uGlide(requireContext(), binding.authImg, R.drawable.img_select)
                    Toast.makeText(requireContext(), resources.getText(R.string.not_selected_photo), Toast.LENGTH_LONG)
                        .show()
                }
            }

        view.authBtnIMG.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            if (activity?.let { it1 -> intent.resolveActivity(it1.packageManager) } != null) {
                getAction.launch(intent)
            }

        }

        view.authImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            if (intent.resolveActivity((activity as AppCompatActivity).packageManager) != null) {
                getAction.launch(intent)
            }
        }



        viewModel.myCountry.observe(viewLifecycleOwner) { countryRES ->
            if (countryRES.isSuccessful) {
                val listCountry = arrayListOf<String>()

                for (i in 0 until countryRES.body()?.data!!.size) {
                    listCountry.add(countryRES.body()?.data!![i].name)
                }

                val items = List(listCountry.size) { i ->
                    SearchableItem(i.toLong(), listCountry[i])
                }

                view.userSpinnerCountry?.items = items

                view.userSpinnerCountry?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            for (i in 0 until countryRES.body()?.data!!.size) {
                                if (countryRES.body()?.data!![i].name == items[position].title) {
                                    posCountry = countryRES.body()?.data!![i].id
                                }
                            }

                        }
                    }

            }
        }


        val listType = resources.getStringArray(R.array.Type)

        val items = List(listType.size) { i ->
            SearchableItem(i.toLong(), listType[i])
        }

        view.userSpinnerType?.items = items

        view.userSpinnerType?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(requireContext(), "NO_SELECT", Toast.LENGTH_SHORT).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    posType = position

                }
            }

        view.nextPrivacy.setOnClickListener {
            inToEmail(PRIVACY_AGREEMENT)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )

        }


        view.sBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }

    @SuppressLint("Recycle")
    private suspend fun filePartScopMetod(ur: ActivityResult) {
        val f = withContext(Dispatchers.IO) {
            File.createTempFile("suffix", "prefix", requireContext().cacheDir)
        }
        withContext(Dispatchers.IO) {
            f.outputStream()
                .use {
                    requireContext().contentResolver.openInputStream(ur.data?.data!!)
                        ?.copyTo(it)
                }
        }
        val requestBody = f.asRequestBody("image/*".toMediaTypeOrNull())
        filePart = MultipartBody.Part.createFormData("img", f.name, requestBody)

    }


    @Suppress("DEPRECATION")
    private fun successRegister() {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_pay)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_pay_yes)
        val buttonNOT = dialog.findViewById<Button>(R.id.dialog_pay_no)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_title_pay)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_pay)
        textTitle.text = resources.getText(R.string.successfully_registered)
        textDescrip.text = resources.getText(R.string.register_message_email)
        buttonYES.text = resources.getText(R.string.yes_verify)
        buttonNOT.text = resources.getText(R.string.confirm_later)
        buttonYES.setOnClickListener {
            val intent = Intent(requireActivity(), StateConfirmActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            findNavController().popBackStack()
            dialog.dismiss()
        }
        buttonNOT.setOnClickListener {
            activity?.onBackPressed()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun stateEditText() {

        binding.authEditPassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                binding.textInputLayout3.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                binding.textInputLayout3.error = null
            }
        }
        binding.authEditPassword2.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                binding.textInputLayout4.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                binding.textInputLayout4.error = null
            }
        }

    }

    private fun alertDialogMessage(title: String, descrip: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_error_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes_error)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_title_error)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_error)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? AuthActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.bottomNavigation?.visibility = View.GONE
    }

    @Suppress("DEPRECATION")
    private fun inToEmail(url: String) {
        val address = Uri.parse(url)
        val openlink = Intent(Intent.ACTION_VIEW, address)
        try {
            startActivity(openlink)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), resources.getText(R.string.no_select), Toast.LENGTH_SHORT).show()
            // Define what your app should do if no activity can handle the intent.
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }
}