package product.promikz.screens.shop

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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.viewModels.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MyUtils
import product.promikz.databinding.FragmentShopChangeBinding
import java.io.ByteArrayOutputStream

class ShopChangeFragment : Fragment() {


    private var _binding: FragmentShopChangeBinding? = null
    private val binding get() = _binding!!

    private var filePart: MultipartBody.Part? = null
    private lateinit var viewModel: HomeViewModel

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val viewModelProfil = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentShopChangeBinding.inflate(inflater, container, false)
        val view = binding


        ref()

        fun rb(value: String): RequestBody {
            return value.toRequestBody("text/plain".toMediaTypeOrNull())
        }


        view.shopCreateBtnPost.setOnClickListener {
            view.shopCreateCons.visibility = View.GONE
            view.shCreateProgress.visibility = View.VISIBLE
            if (view.shopCreateEditName.length() != 0 && view.shopCreateEditDescription.length() != 0) {
                viewModelProfil.updateShopCreate("Bearer $TOKEN_USER",rb("put"),
                    rb(ID_SHOP_USER.toString()),
                    rb(view.shopCreateEditName.text.toString().trim()),
                    rb(view.shopCreateEditDescription.text.toString().trim()),
                    filePart
                )
            } else {
                Toast.makeText(requireContext(), resources.getText(R.string.add_all_fields), Toast.LENGTH_SHORT).show()
            }

        }


        viewModelProfil.myUpdateShopCreate.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                view.shopCreateCons.visibility = View.GONE
                view.shCreateProgress.visibility = View.GONE
                withStyle()
            }else{
                view.shopCreateCons.visibility = View.VISIBLE
                view.shCreateProgress.visibility = View.GONE
            }
        }

        view.shopCreateBtnIMG.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 301)
        }

        view.shopCreateImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 301)
        }

        view.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return view.root
    }

    private fun ref() {
        viewModel.getShops("Bearer $TOKEN_USER", ID_SHOP_USER)
        viewModel.myShopsModels.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                binding.shopCreateEditName.setText(list.body()?.data?.name)
                binding.shopCreateEditDescription.setText(list.body()?.data?.description)
                MyUtils.uGlide(requireContext(), binding.shopCreateImg, list.body()?.data?.img)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun withStyle() {

        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        alertDialogBuilder.setTitle(resources.getText(R.string.successfully))
        alertDialogBuilder.setMessage(R.string.update_shop_text)
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

    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun filePartScopMetod(uri: Uri) {
        try {
//            v.authImg.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.shopCreateImg, uri)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )

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

    private fun startCropActivity(imageUri: Uri?) {

        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setMaxCropResultSize(2200, 2500)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(requireActivity(), this)

    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 301 && resultCode == Activity.RESULT_OK && data != null) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}