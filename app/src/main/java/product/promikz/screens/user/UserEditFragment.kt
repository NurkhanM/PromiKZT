package product.promikz.screens.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import com.prongbang.dialog.SmartDialogFragment
import com.prongbang.dialog.SmartIcon
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.getBrandID
import product.promikz.AppConstants.getCategoryID
import product.promikz.MyUtils
import product.promikz.MyUtils.uLogD
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentUserEditBinding
import product.promikz.screens.create.newProduct.category.CategorySelectFragment
import product.promikz.screens.create.newProduct.counterOne.CounterSelectOneFragment
import tech.hibk.searchablespinnerlibrary.SearchableItem
import java.io.ByteArrayOutputStream

class UserEditFragment : Fragment() {

    private var _binding: FragmentUserEditBinding? = null
    private val binding get() = _binding!!

    private var filePart: MultipartBody.Part? = null
    private lateinit var viewModel: HomeViewModel
    private var posEmail: String? = ""
    private val params = HashMap<String, RequestBody>()

    private var posType: Int? = -1

    lateinit var dialog: Dialog


    @Suppress("DEPRECATION")
    @SuppressLint("Recycle", "CommitTransaction")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentUserEditBinding.inflate(inflater, container, false)
        val view = binding

        AppConstants.COUNTRY_ID = -2
        dialog = Dialog(requireContext())



        view.authBtnIMG.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 300)
        }

        view.authImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 300)
        }


        val listType = resources.getStringArray(R.array.Type)

        val items = List(listType.size) { i ->
            SearchableItem(i.toLong(), listType[i])
        }

        view.userSpinnerType.items = items

        view.userSpinnerType.onItemSelectedListener =
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


        ref()


        view.authBtnPost.setOnClickListener {
            params.clear()


            view.profilteRegisterConstreite.visibility = View.GONE
            view.authBtnPost.visibility = View.GONE
            view.profileProgress.visibility = View.VISIBLE


            params["_method"] = rb("put")

            if (view.authEditName.text?.isNotEmpty() == true) {
                params["name"] = rb(view.authEditName.text.toString().trim())
            }

            if (AppConstants.COUNTRY_ID != -2) {
                params["country_id"] = rb("${AppConstants.COUNTRY_ID}")
            }

            if (view.authEditPassword.text?.isNotEmpty() == true) {
                params["password"] = rb(view.authEditPassword.text.toString())
            }

            if (view.authEditPassword2.text?.isNotEmpty() == true) {
                params["password_confirmation"] = rb(view.authEditPassword2.text.toString())
            }

            uLogD("TEST -> $posType")
            params["type"] = rb(posType.toString())
//                if (view.authEditPhone.text?.isNotEmpty() == true){
//                    params["phone"] = rb(view.authEditPhone.text.toString().trim())
//                }

            fun String.isValidEmail() =
                isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()




            if (view.authEditEmail.text?.isNotEmpty() == true) {
                if (view.authEditEmail.text.toString().trim().isValidEmail()) {
                    if (view.authEditEmail.text.toString().trim() != posEmail) {

//                        dialogEmailCheck(view.authEditEmail.text.toString().trim())
                        params["email"] = rb(view.authEditEmail.text.toString().trim())


                    }

//                    else {
//                        viewModel.updateUserPost(
//                            "Bearer $TOKEN_USER",
//                            params,
//                            filePart
//                        )
//                    }


                } else {
                    view.profilteRegisterConstreite.visibility = View.VISIBLE
                    view.authBtnPost.visibility = View.VISIBLE
                    view.profileProgress.visibility = View.GONE
                    uToast(requireContext(), "Некорректный email")
                }

            } else {
                view.profilteRegisterConstreite.visibility = View.VISIBLE
                view.authBtnPost.visibility = View.VISIBLE
                view.profileProgress.visibility = View.GONE
                uToast(requireContext(), "Пустое поле email")
            }



            viewModel.updateUserPost(
                "Bearer $TOKEN_USER",
                params,
                filePart
            )

        }


        viewModel.myupdateUserPost.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                withStyle()
                view.profilteRegisterConstreite.visibility = View.VISIBLE
                view.authBtnPost.visibility = View.VISIBLE
                view.profileProgress.visibility = View.GONE
            } else {
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                val jsonObjError = JSONObject(jsonObj.getString("errors"))


                for (name in jsonObjError.keys()) {

                    val nameArray = jsonObjError.getJSONArray(name)

                    for (i in 0 until nameArray.length()) {
                        uToast(requireContext(), nameArray.getString(i))
                    }

                }
                view.profilteRegisterConstreite.visibility = View.VISIBLE
                view.authBtnPost.visibility = View.VISIBLE
                view.profileProgress.visibility = View.GONE
            }
        }


        view.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        view.authEditPassword.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                view.textInputLayout3.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                view.textInputLayout3.error = null
            }
        }

        view.authEditPassword2.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 8) {
                view.textInputLayout4.error = resources.getText(R.string.minimum_8)
            } else if (text.length <= 18) {
                view.textInputLayout4.error = null
            }
        }


        view.edtNameCity.setOnClickListener {
            val fragment = CounterSelectOneFragment()
            fragment.setTargetFragment(this, 81)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()
        }

        return view.root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun filePartScopMetod(uri: Uri) {
        try {
//            v.authImg.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.authImg, uri)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            MyUtils.uGlide(requireContext(), binding.authImg, R.drawable.img_select)
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.not_selected_photo),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun sendFileRequest(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()

        filePart = MultipartBody.Part.createFormData(
            "img",
            "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )

    }

    fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun ref() {
        viewModel.getUser("Bearer $TOKEN_USER")
        viewModel.myUser.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                binding.authEditName.setText(list.body()?.data?.name)
                binding.authEditEmail.setText(list.body()?.data?.email)
                posEmail = list.body()?.data?.email
                binding.authEditPhone.setText(list.body()?.data?.phone)
                binding.edtNameCity.setText(list.body()?.data?.country?.name!!)

                if (list.body()?.data?.type == "0") {
                    binding.userSpinnerType.setSelection(
                        list.body()?.data?.type.toString().toInt() + 1
                    )
                } else {
                    binding.textInputLayout7.visibility = View.GONE
                }

                MyUtils.uGlide(requireContext(), binding.authImg, list.body()?.data?.img)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun withStyle() {

        SmartDialogFragment.Builder((activity as AppCompatActivity).supportFragmentManager)
            .setIcon(SmartIcon.SUCCESS)
            .setTitle("УСПЕШНО!!!")
            .setMessage("Было изменено")
            .setPositiveButton("Выйти") {
                getCategoryID = -1
                getBrandID = -1
                activity?.onBackPressed()
            }

            .build()
            .show()
    }


    private fun startCropActivity(imageUri: Uri?) {

        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setMaxCropResultSize(2200, 2500)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireActivity(), this)

    }


    private fun dialogEmailCheck(email: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.fragment_check_email)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.btn_confirm)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_center_aut_title)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_center_aut_desc)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progress)
        val sBackCard = dialog.findViewById<ImageView>(R.id.sBackCard)
        val authEditCode = dialog.findViewById<TextInputEditText>(R.id.authEditCode)

        viewModel.fargotEmail(email.trim())

        textTitle.text = "КОД ПОДТВЕРЖДЕНИЯ"
        textDescrip.text = "Введите код которое мы отправили \nна ваш email ->\n$email"
        buttonYES.text = "Подтвердить"


        sBackCard.setOnClickListener {
            binding.profilteRegisterConstreite.visibility = View.VISIBLE
            binding.authBtnPost.visibility = View.VISIBLE
            binding.profileProgress.visibility = View.GONE
            dialog.dismiss()
        }

        buttonYES.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            buttonYES.visibility = View.GONE
            sBackCard.visibility = View.GONE
            if (authEditCode.text.toString().isNotEmpty()) {
                viewModel.codeCheckEmail(
                    "Bearer $TOKEN_USER",
                    authEditCode.text.toString().trim()
                )
            } else {
                progressBar.visibility = View.GONE
                buttonYES.visibility = View.VISIBLE
                sBackCard.visibility = View.VISIBLE
                uToast(requireContext(), "Пустое поле")
            }

        }

        viewModel.myFargotEmail.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                Toast.makeText(requireContext(), "${list.body()?.message}", Toast.LENGTH_SHORT)
                    .show()

            } else {

                try {

                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))


                    for (name in jsonObjError.keys()) {

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()) {
                            uToast(requireContext(), nameArray.getString(i))
                        }

                    }

                    binding.profilteRegisterConstreite.visibility = View.VISIBLE
                    binding.authBtnPost.visibility = View.VISIBLE
                    binding.profileProgress.visibility = View.GONE

                    dialog.dismiss()

                } catch (e: JSONException) {
                    uToast(requireContext(), "Error backend")
                }

            }


        }


        viewModel.myCodeCheckEmail.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                uToast(requireContext(), list.body()?.message)
                params["email"] = rb(email)
                viewModel.updateUserPost(
                    "Bearer $TOKEN_USER",
                    params,
                    filePart
                )
                dialog.dismiss()
            } else {
                progressBar.visibility = View.GONE
                buttonYES.visibility = View.VISIBLE
                sBackCard.visibility = View.VISIBLE
                uToast(requireContext(), "Неправильный код")
            }

        }

        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300 && resultCode == RESULT_OK && data != null) {
            startCropActivity(data.data)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    filePartScopMetod(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    uToast(requireContext(), "Opps image eerror")
                }
            } else {
                uToast(requireContext(), resources.getString(R.string.not_selected_photo))
            }
        } else if (requestCode == 81 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            if (result != null) {
                binding.edtNameCity.setText(result)
            }
        }
    }
}