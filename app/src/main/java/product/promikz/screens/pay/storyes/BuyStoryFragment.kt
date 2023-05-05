package product.promikz.screens.pay.storyes

import android.annotation.SuppressLint
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
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.ID_PAY
import product.promikz.AppConstants.USER_TYPE
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.databinding.FragmentBuyStoryBinding
import product.promikz.viewModels.HomeViewModel
import java.io.ByteArrayOutputStream

class BuyStoryFragment : Fragment() {

    private var _binding: FragmentBuyStoryBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModels: HomeViewModel

    private var filePartAll = ArrayList<MultipartBody.Part?>()
    private var partBanner: MultipartBody.Part? = null

    private var partBanner1: MultipartBody.Part? = null
    private var partBanner2: MultipartBody.Part? = null
    private var partBanner3: MultipartBody.Part? = null
    private var partBanner4: MultipartBody.Part? = null
    private var partBanner5: MultipartBody.Part? = null

    var boolean = false

    private var boolean1 = false
    private var boolean2 = false
    private var boolean3 = false
    private var boolean4 = false
    private var boolean5 = false

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
        _binding = FragmentBuyStoryBinding.inflate(inflater, container, false)
        val view = binding

        dialog = Dialog(requireContext())
        dialogPay = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())



        view.imageBanner.setOnClickListener {
            setNormalSingleButton()
        }

        view.imageBanner1.setOnClickListener {
            setNormalSingleButtonExtra1(view.imageBanner1)
        }

        view.imageBanner2.setOnClickListener {
            setNormalSingleButtonExtra2(view.imageBanner2)
        }

        view.imageBanner3.setOnClickListener {
            setNormalSingleButtonExtra3(view.imageBanner3)
        }

        view.imageBanner4.setOnClickListener {
            setNormalSingleButtonExtra4(view.imageBanner4)
        }

        view.imageBanner5.setOnClickListener {
            setNormalSingleButtonExtra5(view.imageBanner5)
        }

        view.btnPlus.setOnClickListener {
            if (view.storyPage1.visibility != View.VISIBLE) {
                view.storyPage1.visibility = View.VISIBLE
            } else if (view.storyPage2.visibility != View.VISIBLE) {
                view.storyPage2.visibility = View.VISIBLE
            } else if (view.storyPage3.visibility != View.VISIBLE) {
                view.storyPage3.visibility = View.VISIBLE
            } else if (view.storyPage4.visibility != View.VISIBLE) {
                view.storyPage4.visibility = View.VISIBLE
            } else if (view.storyPage5.visibility != View.VISIBLE) {
                view.storyPage5.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.maximum_5),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        view.btnMinus2.setOnClickListener {
            binding.storyPage2.visibility = View.GONE
            boolean2 = false
            partBanner2 = null
        }

        view.btnMinus3.setOnClickListener {
            binding.storyPage3.visibility = View.GONE
            boolean3 = false
            partBanner3 = null
        }

        view.btnMinus4.setOnClickListener {
            binding.storyPage4.visibility = View.GONE
            boolean4 = false
            partBanner4 = null
        }

        view.btnMinus5.setOnClickListener {
            binding.storyPage5.visibility = View.GONE
            boolean5 = false
            partBanner5 = null
        }


        view.buttonBuy.setOnClickListener {

            if (boolean && view.textName.text.isNotEmpty() && view.textDescription.text.isNotEmpty() &&
                boolean1 && view.textSite1.text.isNotEmpty() && isForStory()
            ) {
                map.clear()
                isAddFilpart()
//                checkIsNull()
                ID_PAY = 7
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
                        .navigate(R.id.action_buyStoryFragment_to_payFragment)
                }
            })
        view.clickUpdateBackCard.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_buyStoryFragment_to_payFragment)
        }


        viewModels.myServices.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {

                for (i in 0 until list?.body()?.data?.size!!) {

                    if (ID_PAY == list.body()?.data?.get(i)?.id) {
                        for (uID in 0 until list.body()?.data?.get(i)?.type!!.size) {
                            if (USER_TYPE == list.body()?.data?.get(i)?.type!![uID]) {
                                map["payId"] = ID_PAY.toString()
                                map["name"] = view.textName.text.toString()
                                map["description"] = view.textDescription.text.toString()
                                viewModels.payGenerateStory(
                                    "Bearer ${AppConstants.TOKEN_USER}",
                                    map,
                                    filePartAll,
                                    partBanner
                                )
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

        viewModels.myPayGenerateStory.observe(viewLifecycleOwner) { list ->
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
                    jsonObj.getString("message").toString(), " Не доступно! "
//                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }
        }

        return view.root
    }

    private fun isAddFilpart() {
        filePartAll.add(partBanner)

        if (partBanner1 != null){
            map["stories[0][link]"] = binding.textSite1.text.toString()
            filePartAll.add(partBanner1)
        }

        if (partBanner2 != null){
            map["stories[1][link]"] = binding.textSite2.text.toString()
            filePartAll.add(partBanner2)
        }

        if (partBanner3 != null){
            map["stories[2][link]"] = binding.textSite3.text.toString()
            filePartAll.add(partBanner3)
        }

        if (partBanner4 != null){
            map["stories[3][link]"] = binding.textSite4.text.toString()
            filePartAll.add(partBanner4)
        }
        if (partBanner5 != null){
            map["stories[4][link]"] = binding.textSite5.text.toString()
            filePartAll.add(partBanner5)
        }
    }

    private fun isForStory(): Boolean {

        var boolean = true

        if (boolean1) {
            if (!isValidStory()) {
                boolean = false
            }
        }
        if (boolean2) {
            if (!isValidStory2()) {
                boolean = false
            }
        }
        if (boolean3) {
            if (!isValidStory3()) {
                boolean = false
            }
        }
        if (boolean4) {
            if (!isValidStory4()) {
                boolean = false
            }
        }
        if (boolean5) {
            if (!isValidStory5()) {
                boolean = false
            }
        }

        return boolean

    }

    private fun isValidStory(): Boolean {
        return binding.textSite1.text.isNotEmpty()
    }

    private fun isValidStory2(): Boolean {
        return binding.textSite2.text.isNotEmpty()
    }

    private fun isValidStory3(): Boolean {
        return binding.textSite3.text.isNotEmpty()
    }

    private fun isValidStory4(): Boolean {
        return binding.textSite4.text.isNotEmpty()
    }

    private fun isValidStory5(): Boolean {
        return binding.textSite5.text.isNotEmpty()
    }


    private fun checkIsNull() {

        if (binding.storyPage1.visibility == View.VISIBLE) {
            if (binding.textSite1.text?.isNotEmpty() == true) {

            }
        }

        if (binding.storyPage2.visibility == View.VISIBLE) {
            if (binding.textSite2.text?.isNotEmpty() == true) {

            }
        }

        if (binding.storyPage3.visibility == View.VISIBLE) {
            if (binding.textSite3.text?.isNotEmpty() == true) {

            }
        }

        if (binding.storyPage4.visibility == View.VISIBLE) {
            if (binding.textSite4.text?.isNotEmpty() == true) {

            }
        }

        if (binding.storyPage5.visibility == View.VISIBLE) {
            if (binding.textSite5.text?.isNotEmpty() == true) {

            }
        }

    }


    private fun setNormalSingleButton() {

        TedImagePicker.with(requireContext())
            .start { uri ->
                showSingleImage(uri)
            }
    }

    private fun setNormalSingleButtonExtra1(image: ImageView) {
        TedImagePicker.with(requireContext())
            .start { uri ->
//                if (boolean1) {
//                    filePartAll.removeAt(0)
//                }
                partBanner1 = showSingleImageExtra(uri, image, 1)
//                filePartAll.add()
            }
    }

    private fun setNormalSingleButtonExtra2(image: ImageView) {
        TedImagePicker.with(requireContext())
            .start { uri ->
//                if (boolean2) {
//                    filePartAll.removeAt(1)
//                }
                partBanner2 = showSingleImageExtra(uri, image, 2)
//                filePartAll.add()
            }
    }

    private fun setNormalSingleButtonExtra3(image: ImageView) {
        TedImagePicker.with(requireContext())
            .start { uri ->
//                if (boolean3) {
//                    filePartAll.removeAt(2)
//                }
                partBanner3 = showSingleImageExtra(uri, image, 3)
//                filePartAll.add()
            }
    }

    private fun setNormalSingleButtonExtra4(image: ImageView) {
        TedImagePicker.with(requireContext())
            .start { uri ->
//                if (boolean4) {
//                    filePartAll.removeAt(3)
//                }
                partBanner4 = showSingleImageExtra(uri, image, 4)
//                filePartAll.add()
            }
    }

    private fun setNormalSingleButtonExtra5(image: ImageView) {
        TedImagePicker.with(requireContext())
            .start { uri ->
//                if (boolean4) {
//                    filePartAll.removeAt(4)
//                }
                partBanner5 = showSingleImageExtra(uri, image, 5)
//                filePartAll.add()
            }
    }


    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun showSingleImage(uri: Uri) {

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
            MyUtils.uGlide(requireContext(), binding.imageBanner, R.drawable.image_add)
            Toast.makeText(requireContext(), "ErrorImage", Toast.LENGTH_LONG)
                .show()


        }

    }

    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun showSingleImageExtra(uri: Uri, image: ImageView, num: Int): MultipartBody.Part? {

//        var part: MultipartBody.Part? = null

        try {
//            image.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), image, uri)

//            part =

            when (num) {
                1 -> {
                    boolean1 = true
                }

                2 -> {
                    boolean2 = true
                }

                3 -> {
                    boolean3 = true
                }

                4 -> {
                    boolean4 = true
                }

                5 -> {
                    boolean5 = true
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            MyUtils.uGlide(requireContext(), image, R.drawable.image_add)
            Toast.makeText(requireContext(), "ErrorImage", Toast.LENGTH_LONG)
                .show()


        }


        return sendFileRequestExtra(
            MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                uri
            ), num
        )

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


    private fun sendFileRequestExtra(image: Bitmap, num: Int): MultipartBody.Part {
        val part: MultipartBody.Part?
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()

        part = MultipartBody.Part.createFormData(
            "stories[${num - 1}][img]", "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )

        return part

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


}