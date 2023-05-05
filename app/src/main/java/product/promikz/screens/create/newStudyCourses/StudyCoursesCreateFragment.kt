package product.promikz.screens.create.newStudyCourses

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prongbang.dialog.SmartDialogFragment
import com.prongbang.dialog.SmartIcon
import com.theartofdev.edmodo.cropper.CropImage
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.ButtonGravity
import gun0912.tedimagepicker.builder.type.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.MainActivity
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.databinding.FragmentCheckUserBinding
import product.promikz.databinding.FragmentCreateProductBinding
import product.promikz.databinding.FragmentStudyCoursesCreateBinding
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.screens.create.CreateViewModel
import product.promikz.screens.create.newProduct.ProductCreateAdapter
import product.promikz.screens.create.newProduct.country.CounterSelectFragment
import product.promikz.viewModels.HomeViewModel
import java.io.File

class StudyCoursesCreateFragment : Fragment() {

    lateinit var dialog: Dialog

    private var _binding: FragmentStudyCoursesCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewCreate: RecyclerView
    lateinit var adapterProduct: ProductCreateAdapter

    private var selectedUriList: ArrayList<Uri>? = null
    private var selectedUriList2: ArrayList<Uri>? = null
    private var uri: Uri? = null
    private var filePart: MultipartBody.Part? = null
    private var stateSelectImageFirst = false
    private var stateSelectImageMulti = false
    private lateinit var filePart2: List<MultipartBody.Part>

    private lateinit var productModel: CreateViewModel


    private val params = HashMap<String, RequestBody>()
    private val fields = HashMap<String, RequestBody>()

    private lateinit var viewModel: HomeViewModel


    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        productModel = ViewModelProvider(this)[CreateViewModel::class.java]
        AppConstants.COUNTRY_ID = 0
        if (savedInstanceState != null) {
            selectedUriList2 = savedInstanceState.getParcelableArrayList("AVATAR")
            uri = savedInstanceState.getParcelable("AVATAR2")!!
        }
        _binding = FragmentStudyCoursesCreateBinding.inflate(inflater, container, false)


        dialog = Dialog(requireContext())

        val firsRas = resources.getStringArray(R.array.RasFirst)
        val arrayAdapterFirsRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, firsRas)
        binding.spinnerFirsRas.adapter = arrayAdapterFirsRas

        val monthRas = resources.getStringArray(R.array.RasMonth)
        val arrayAdapterMonthRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, monthRas)
        binding.spinnerMonthRas.adapter = arrayAdapterMonthRas




        recyclerViewCreate = binding.rvCreate
        adapterProduct = ProductCreateAdapter(object : IClickListnearUpdateImage {
            override fun clickListener(baseID: String, index: Int) {
                adapterProduct.deleteMyEducations(index)
                if (selectedUriList2?.size in 0..9) {
                    binding.cardView3.visibility = View.VISIBLE
                } else {
                    binding.cardView3.visibility = View.GONE
                }
                binding.txtSizeList.text = selectedUriList2?.size.toString()
            }

        })
        recyclerViewCreate.adapter = adapterProduct
        recyclerViewCreate.setHasFixedSize(true)
        setNormalSingleButton()
        setNormalMultiButton()

        binding.edtNameCity.setOnClickListener {

            val fragment = CounterSelectFragment()
            fragment.setTargetFragment(this, 188)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()

        }


        productModel.myProductCreate.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                withStyle()
            } else {
                binding.createLoader.visibility = View.GONE
                binding.clickUpdateBackCard.visibility = View.VISIBLE
                binding.fonCreate.visibility = View.VISIBLE
                binding.textTitle.text = resources.getString(R.string.create_ads)

                try {

                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())

                    alertDialogCancel(

                        jsonObj.getString("errors").toString().replace("{\"", "").replace("\"}", "")
                            .replace("\"],\"", "\n").replace("\":[\"", " -> ")

                    )

                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error Server", Toast.LENGTH_SHORT).show()
                }
            }

        }


        binding.postProductBTN.setOnClickListener {

            binding.createLoader.visibility = View.VISIBLE
            binding.textTitle.text = resources.getString(R.string.wait)
            binding.clickUpdateBackCard.visibility = View.GONE
            binding.fonCreate.visibility = View.GONE

            if (binding.textNewProductName.length() != 0 &&
                binding.textNewProductDescription.length() != 0 &&
                AppConstants.COUNTRY_ID != 0
            ) {
                if (stateSelectImageFirst && stateSelectImageMulti) {

                    uploadProduct()

                } else {

                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.selected_photo),
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    binding.createLoader.visibility = View.GONE
                    binding.clickUpdateBackCard.visibility = View.VISIBLE
                    binding.fonCreate.visibility = View.VISIBLE
                    binding.textTitle.text = resources.getString(R.string.all_courses)
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.add_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
                binding.createLoader.visibility = View.GONE
                binding.clickUpdateBackCard.visibility = View.VISIBLE
                binding.fonCreate.visibility = View.VISIBLE
                binding.textTitle.text = resources.getString(R.string.all_courses)

            }

        }


        binding.textNewProductName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                v.clearFocus()
                closeKeyBoard(v)
                return@OnKeyListener true
            }
            false
        })


        if (uri != null) {
            showSingleImage(uri!!)
        }

        if (selectedUriList2 != null) {
            showMultiImage(selectedUriList2!!)
        }

        binding.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        binding.swichRassrochka.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.vRassrochku2.visibility = View.VISIBLE
            } else {
                binding.vRassrochku2.visibility = View.GONE
            }
        }


        binding.btnResultRas.setOnClickListener {
            if (binding.edtSumRas.text.isNotEmpty()) {

                when (binding.spinnerFirsRas.selectedItemPosition) {
                    0 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 5).toString()

                    1 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 10).toString()

                    2 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 15).toString()

                    3 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 20).toString()

                    4 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 30).toString()

                    5 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 40).toString()

                    6 -> binding.textFirsRas.text =
                        (binding.edtSumRas.text.toString().toInt() / 100 * 50).toString()
                }

                val sam =
                    (binding.edtSumRas.text.toString().toInt() - binding.textFirsRas.text.toString()
                        .toInt())

                when (binding.spinnerMonthRas.selectedItemPosition) {
                    0 -> binding.textMonthRas.text = (sam / 3).toString()
                    1 -> binding.textMonthRas.text = (sam / 6).toString()
                    2 -> binding.textMonthRas.text = (sam / 12).toString()
                    3 -> binding.textMonthRas.text = (sam / 24).toString()
                    4 -> binding.textMonthRas.text = (sam / 48).toString()
                }

                binding.txtEndMany.visibility = View.VISIBLE
                binding.txtEndMany2.visibility = View.VISIBLE

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.enter_installment_amount),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }

        return binding.root
    }


    private fun fieldsStatus(arr: ArrayList<View>): Boolean {

        var state = false
        val massiv = ArrayList<Boolean>()

        for (i in 0 until arr.size) {
            if (arr[i] is EditText) {
                if ((arr[i] as EditText).length() != 0) {
                    massiv.add(true)
                } else {
                    massiv.add(false)
                }
            }
        }

        for (i in 0 until massiv.size) {
            state = massiv[i]
        }

        return state
    }

    private fun rbView(view: View): String {
        var value = " "

        if (view is EditText) {
            value = view.text.toString()
        }
        if (view is Spinner) {
            value = view.selectedItemPosition.toString()
        }

        return value
    }

    private fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun uploadProduct() {
        params.clear()
        params["type"] = rb("2")
        params["name"] = rb(binding.textNewProductName.text.toString().trim())
        params["country_id"] = rb(AppConstants.COUNTRY_ID.toString())
        if (binding.textNewProductPrice.length() != 0) {
            params["price"] = rb((binding.textNewProductPrice).text.toString().trim())
        } else {
            params["price"] = rb("0")
        }

        params["description"] = rb(binding.textNewProductDescription.text.toString().trim())

        if (binding.swichRassrochka.isChecked && binding.edtSumRas.text.isNotEmpty()) {

            params["installment[success]"] = rb("1")
            params["installment[price]"] = rb(binding.edtSumRas.text.toString())

            params["installment[firstPercent]"] = rb(firstPercent())
            params["installment[type]"] = rb(instalmentType())

        }

        if (binding.swichReview.isChecked) {
            params["review"] = rb("1")
        } else {
            params["review"] = rb("0")
        }


        productModel.pushProductCreate(
            "Bearer ${AppConstants.TOKEN_USER}",
            params,
            fields,
            filePart,
            filePart2
        )
    }

    private fun firstPercent(): String {
        return when (binding.spinnerFirsRas.selectedItemPosition) {
            0 -> "5"
            1 -> "10"
            2 -> "15"
            3 -> "20"
            4 -> "30"
            5 -> "40"
            else -> "50"
        }
    }

    private fun instalmentType(): String {
        return when (binding.spinnerMonthRas.selectedItemPosition) {
            0 -> "3"
            1 -> "6"
            2 -> "12"
            3 -> "24"
            else -> "48"
        }
    }


    private fun closeKeyBoard(view: View) {
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun setNormalSingleButton() {
        binding.ivImage.setOnClickListener {
            TedImagePicker.with(requireContext())
                .start { uri ->
                    this.uri = uri
                    showSingleImage(uri)
                }
        }
    }

    private fun setNormalMultiButton() {
        binding.btnNormalMulti.setOnClickListener {
            TedImagePicker.with(requireContext())
                .max(10, resources.getText(R.string.maximum_10).toString())
                .mediaType(MediaType.IMAGE)
                //.scrollIndicatorDateFormat("YYYYMMDD")
                .buttonGravity(ButtonGravity.BOTTOM)
                //.buttonBackground(R.drawable.btn_sample_done_button)
                //.buttonTextColor(R.color.sample_yellow)
                .errorListener { message -> Log.d("ted", "message: $message") }
                .selectedUri(selectedUriList)
                .startMultiImage { list: List<Uri> ->
                    selectedUriList2 = list as ArrayList<Uri>
                    showMultiImage(list)
                    if (selectedUriList2?.size in 0..9) {
                        binding.cardView3.visibility = View.VISIBLE
                    } else {
                        binding.cardView3.visibility = View.GONE
                    }
                    binding.txtSizeList.text = selectedUriList2?.size.toString()
                }
        }
    }


    @SuppressLint("Recycle")
    private fun showSingleImage(uri: Uri) {
        try {
            binding.ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.ivImage, uri)
            val f = File.createTempFile("suffix", "prefix", requireContext().cacheDir)
            f.outputStream()
                .use {
                    requireContext().contentResolver.openInputStream(uri)
                        ?.copyTo(it)
                }

            val requestBody = f.asRequestBody("image/*".toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("img", f.name, requestBody)
            stateSelectImageFirst = true

        } catch (e: Exception) {
            e.printStackTrace()
            Glide.with(requireContext()).load(R.drawable.img_select)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .centerCrop()
                .into(binding.ivImage)
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.not_selected_photo),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    @SuppressLint("Recycle")
    private fun showMultiImage(uriList: ArrayList<Uri>) {
        try {
            this.selectedUriList = uriList
            adapterProduct.setList(uriList)
            val list: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in 0 until uriList.size) {
                val f = File.createTempFile("suffix", "prefix", requireContext().cacheDir)
                f.outputStream()
                    .use {
                        requireContext().contentResolver.openInputStream(uriList[i])?.copyTo(it)
                        val requestBody = f.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        list.add(MultipartBody.Part.createFormData("images[]", f.name, requestBody))
                    }
                filePart2 = list
                stateSelectImageMulti = true
            }


        } catch (e: Exception) {
            e.printStackTrace()
            this.selectedUriList = uriList
            adapterProduct.setList(uriList)
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.not_selected_photo),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelableArrayList("AVATAR", selectedUriList2)
        outState.putParcelable("AVATAR2", uri)

    }

    @Suppress("DEPRECATION")
    private fun withStyle() {

        SmartDialogFragment.Builder((activity as AppCompatActivity).supportFragmentManager)
            .setIcon(SmartIcon.SUCCESS)
            .setTitle(resources.getText(R.string.successfully))
            .setMessage(resources.getText(R.string.yeah_product))
            .setPositiveButton(resources.getText(R.string.exit)) {
                AppConstants.getCategoryID = -1
                AppConstants.getBrandID = -1
                activity?.onBackPressed()
            }

            .build()
            .show()
    }

    private fun alertDialogCancel(em: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_error_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes_error)
        val textError = dialog.findViewById<TextView>(R.id.txt_descript_error)
        textError.text = em
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 188 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("textCountryName")
            if (result != null) {
                binding.edtNameCity.setText(result)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}