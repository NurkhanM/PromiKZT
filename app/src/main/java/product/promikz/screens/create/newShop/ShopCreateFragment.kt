package product.promikz.screens.create.newShop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.viewModels.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.MyUtils
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentShopCreateBinding
import java.io.ByteArrayOutputStream

class ShopCreateFragment : Fragment() {

    private var _binding: FragmentShopCreateBinding? = null
    private val binding get() = _binding!!

    private var filePart: MultipartBody.Part? = null
    private var stateSelectImageFirst = false

    @Suppress("DEPRECATION")
    @SuppressLint("QueryPermissionsNeeded", "Recycle")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val viewModelProfil = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentShopCreateBinding.inflate(inflater, container, false)
        val view = binding

        fun rb(value: String): RequestBody {
            return value.toRequestBody("text/plain".toMediaTypeOrNull())
        }


        view.shopCreateBtnPost.setOnClickListener {
            view.shopCreateCons.visibility = View.GONE
            view.shCreateProgress.visibility = View.VISIBLE
            if (view.shopCreateEditName.length() != 0 && view.shopCreateEditDescription.length() != 0) {
                viewModelProfil.pushShopCreate(
                    "Bearer $TOKEN_USER",
                    rb(view.shopCreateEditName.text.toString().trim()),
                    rb(view.shopCreateEditDescription.text.toString().trim()),
                    filePart
                )

            } else {
                view.shopCreateCons.visibility = View.VISIBLE
                view.shCreateProgress.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.add_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }



        viewModelProfil.myShopCreate.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                withStyle()
                ID_SHOP_USER = list.body()?.data?.id!!
            } else {
                view.shopCreateCons.visibility = View.VISIBLE
                view.shCreateProgress.visibility = View.GONE
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                val jsonObjError = JSONObject(jsonObj.getString("errors"))
                var message = ""

                for (name in jsonObjError.keys()){

                    val nameArray = jsonObjError.getJSONArray(name)

                    for (i in 0 until nameArray.length()){
                        message = nameArray.getString(i)
                    }

                }

                uToast(requireContext(), message)
            }
        }


        view.shopCreateBtnIMG.setOnClickListener {
            stateSelectImageFirst = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 305)
        }

        view.shopCreateImg.setOnClickListener {
            stateSelectImageFirst = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 305)
        }

        view.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return view.root
    }

    @Suppress("DEPRECATION")
    private fun withStyle() {

        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        alertDialogBuilder.setTitle(resources.getText(R.string.successfully))
        alertDialogBuilder.setMessage(R.string.register_message_shopCreate)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            resources.getText(R.string.continue_app)
        ) { dialog, _ ->
            activity?.onBackPressed()
            dialog.cancel()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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



    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun filePartScopMetod(uri: Uri) {
        try {
            binding.shopCreateImg.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.shopCreateImg, uri)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )
            stateSelectImageFirst = true
        } catch (e: Exception) {
            e.printStackTrace()
            MyUtils.uGlide(requireContext(), binding.shopCreateImg, R.drawable.img_select)
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.not_selected_photo),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }



    private fun startCropActivity(imageUri: Uri?) {

        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
//            .setAspectRatio(1, 1)
            .setMaxCropResultSize(1100, 1300)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireActivity(), this)

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 305 && resultCode == Activity.RESULT_OK && data != null) {
            startCropActivity(data.data)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    filePartScopMetod(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    uToast(requireContext(), "Opps image eerror")
                }
            } else {
                uToast(requireContext(), resources.getString(R.string.not_selected_photo))
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}