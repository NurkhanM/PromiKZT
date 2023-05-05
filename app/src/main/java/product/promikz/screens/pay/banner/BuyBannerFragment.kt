package product.promikz.screens.pay.banner

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.ID_PAY
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.databinding.FragmentBuyBannerBinding
import product.promikz.viewModels.HomeViewModel
import java.io.ByteArrayOutputStream

class BuyBannerFragment : Fragment() {

    private var _binding: FragmentBuyBannerBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModels: HomeViewModel

    private var partBanner: MultipartBody.Part? = null
    var boolean = false

    lateinit var dialog: Dialog
    private lateinit var dialogPay: Dialog
    private lateinit var dialogLoader: Dialog


    private val map: HashMap<String, String> = hashMapOf()

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentBuyBannerBinding.inflate(inflater, container, false)
        val view = binding


        dialog = Dialog(requireContext())
        dialogPay = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())


        binding.imageBanner.setOnClickListener {
            boolean = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 302)
        }

        view.buttonBuy.setOnClickListener {
            if (boolean && view.textURL.text.isNotEmpty()) {
                ID_PAY = 5
                viewModels.getServices()
                alertDialogLoader()
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(view.root)
                        .navigate(R.id.action_buyBannerFragment_to_payFragment)
                }
            })

        view.clickUpdateBackCard.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_buyBannerFragment_to_payFragment)
        }


        viewModels.myServices.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {

                for (i in 0 until list?.body()?.data?.size!!) {

                    if (ID_PAY == list.body()?.data?.get(i)?.id) {
                        for (uID in 0 until list.body()?.data?.get(i)?.type!!.size) {
                            if (AppConstants.USER_TYPE == list.body()?.data?.get(i)?.type!![uID]) {
                                map.clear()
                                map["payId"] = ID_PAY.toString()
                                map["link"] = view.textURL.text.toString()
                                viewModels.payGenerateBanner("Bearer $TOKEN_USER", map, partBanner)
                                break
                            } else {
                                dialogLoader.dismiss()
                                alertDialogMessage(
                                    "Ошибка",
                                    "Не доступно"
                                )
                            }
                        }
                        break
                    }
                }

            } else {
                dialogLoader.dismiss()
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(),
                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }


        }

        viewModels.myPayGenerateBanner.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                dialogLoader.dismiss()
                alertDialogPay(
                    list.body()?.data?.order?.description.toString(),
                    "Ваш тариф составляет " +
                            "${list.body()?.data?.order?.price} ${list.body()?.data?.order?.currency} за ${list.body()?.data?.order?.term} дней",
                    list.body()?.data?.url.toString()
                )
            } else {
                dialogLoader.dismiss()
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(), " mm "
//                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }
        }




        return view.root
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

    @Suppress("DEPRECATION")
    private fun alertDialogPay(title: String, descrip: String, url: String) {

        dialogPay.setCancelable(false)
        dialogPay.setCanceledOnTouchOutside(false)
        dialogPay.setContentView(R.layout.dialog_pay)
        dialogPay.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialogPay.findViewById<Button>(R.id.dialog_pay_yes)
        val buttonNOT = dialogPay.findViewById<Button>(R.id.dialog_pay_no)
        val textTitle = dialogPay.findViewById<TextView>(R.id.txt_title_pay)
        val textDescrip = dialogPay.findViewById<TextView>(R.id.txt_descript_pay)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(requireContext(), browserIntent, null)
            dialogPay.dismiss()
            activity?.onBackPressed()
        }
        buttonNOT.setOnClickListener {
            dialogPay.dismiss()
        }
        dialogPay.show()
    }

    private fun alertDialogLoader() {

        dialogLoader.setCancelable(false)
        dialogLoader.setCanceledOnTouchOutside(false)
        dialogLoader.setContentView(R.layout.dialog_loader)
        dialogLoader.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoader.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 302 && resultCode == Activity.RESULT_OK && data != null) {
            startCropActivity(data.data)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    filePartScopMetod(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    MyUtils.uToast(requireContext(), "Opps image eerror")
                }
            } else {
                MyUtils.uToast(requireContext(), resources.getString(R.string.not_selected_photo))
            }
        }
    }


    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun filePartScopMetod(uri: Uri) {
        try {
            binding.imageBanner.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.imageBanner, uri)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )
            boolean = true
        } catch (e: Exception) {
            e.printStackTrace()
            MyUtils.uGlide(requireContext(), binding.imageBanner, R.drawable.img_select)
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

        partBanner = MultipartBody.Part.createFormData(
            "img",
            "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )

    }


    private fun startCropActivity(imageUri: Uri?) {

        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
//            .setAspectRatio(2, 1)
            .setMaxCropResultSize(4500, 3000)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireActivity(), this)

    }


}