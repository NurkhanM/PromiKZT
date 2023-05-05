package product.promikz.screens.create.newTradeIn

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.prongbang.dialog.SmartDialogFragment
import com.prongbang.dialog.SmartIcon
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.databinding.FragmentCreateProductBinding
import product.promikz.databinding.FragmentTradeInCreateBinding
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.screens.create.CreateViewModel
import product.promikz.screens.create.newProduct.ProductCreateAdapter
import product.promikz.screens.create.newProduct.brand.BrandProductSelectFragment
import product.promikz.screens.create.newProduct.category.CategorySelectFragment
import product.promikz.screens.create.newProduct.country.CounterSelectFragment
import product.promikz.viewModels.HomeViewModel
import java.io.ByteArrayOutputStream
import java.io.File

class TradeInCreateFragment : Fragment() {

    private var _binding: FragmentTradeInCreateBinding? = null
    private val binding get() = _binding!!


    lateinit var dialog: Dialog
    private lateinit var recyclerViewCreate: RecyclerView
    lateinit var adapterProduct: ProductCreateAdapter

    private var selectedUriList: ArrayList<Uri>? = null
    private var selectedUriList2: ArrayList<Uri>? = null
    private var caIndex: Int? = 0
    private var filePart: MultipartBody.Part? = null
    private var stateSelectImageFirst = false
    private var stateSelectImageMulti = false
    private lateinit var filePart2: List<MultipartBody.Part>
    private lateinit var productModel: CreateViewModel
    private val params = HashMap<String, RequestBody>()
    private val fields = HashMap<String, RequestBody>()
    private lateinit var viewModel: HomeViewModel
    private val ed = ArrayList<View>()
    private var isHarakter: Boolean = true

    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        productModel = ViewModelProvider(this)[CreateViewModel::class.java]
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        productModel = ViewModelProvider(this)[CreateViewModel::class.java]
        AppConstants.COUNTRY_ID = 0
        AppConstants.getCategoryID = -1
        AppConstants.getBrandID = -1
        if (savedInstanceState != null) {
            selectedUriList2 = savedInstanceState.getParcelableArrayList("AVATAR")
            caIndex = savedInstanceState.getInt("CA_INDEX")
        }
        _binding = FragmentTradeInCreateBinding.inflate(inflater, container, false)

        dialog = Dialog(requireContext())

        val state = resources.getStringArray(R.array.State)
        val arrayAdapterState = ArrayAdapter(requireContext(), R.layout.item_dropdown, state)
        binding.spinnerState.adapter = arrayAdapterState

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
        binding.ivImage.setOnClickListener {
            stateSelectImageFirst = false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 302)
        }
        setNormalMultiButton()

        binding.textNewProductCategory.setOnClickListener {

            val fragment = CategorySelectFragment()
            fragment.setTargetFragment(this, 50)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()


        }

        binding.textNewProductBrand.setOnClickListener {

            val fragment = BrandProductSelectFragment()
            fragment.setTargetFragment(this, 63)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()

        }

        binding.edtNameCity.setOnClickListener {
            val fragment = CounterSelectFragment()
            fragment.setTargetFragment(this, 88)
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
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))
                    var message = ""

                    for (name in jsonObjError.keys()){

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()){
                            message = nameArray.getString(i)
                        }

                    }

                    alertDialogCancel(message)

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

            if (binding.textNewProductName.length() != 0 && binding.textNewProductDescription.length() != 0 &&
                binding.textNewProductCategory.length() != 0 && fieldsStatus(ed) && AppConstants.COUNTRY_ID != 0
            ) {
                if (stateSelectImageFirst && stateSelectImageMulti) {
                    viewModel.myGetCategoryID.observe(viewLifecycleOwner) { li ->
                        for (i in 0 until li.body()?.data?.fields?.size!!.toInt()) {
                            fields["fields[${li.body()?.data?.fields?.get(i)?.pivot_id.toString()}]"] =
                                rb(rbView(ed[i]))
                        }

                    }

                    uploadProduct()

                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.selected_photo),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.createLoader.visibility = View.GONE
                    binding.clickUpdateBackCard.visibility = View.VISIBLE
                    binding.fonCreate.visibility = View.VISIBLE
                    binding.textTitle.text = resources.getString(R.string.create_ads)
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
                binding.textTitle.text = resources.getString(R.string.create_ads)

            }

        }


//        binding.textNewProductName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                v.clearFocus()
//                closeKeyBoard(v)
//                return@OnKeyListener true
//            }
//            false
//        })


        if (selectedUriList2 != null) {
            showMultiImage(selectedUriList2!!)
        }
        if (caIndex != 0) {
            uploadProductElectronic(caIndex!!)
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


        binding.txtDop.setOnClickListener {
            if (isHarakter) {
                isHarakter = false
                binding.imgHarakter.setImageResource(R.drawable.bottom_right)
                binding.inElectron.visibility = View.GONE
            } else {
                isHarakter = true
                binding.imgHarakter.setImageResource(R.drawable.bottom)
                binding.inElectron.visibility = View.VISIBLE
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
        if (view is Slider) {
            value = view.value.toInt().toString()
        }

        return value
    }

    private fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun uploadProduct() {
        params.clear()
        params["type"] = rb("1")
        params["name"] = rb(binding.textNewProductName.text.toString().trim())
        params["country_id"] = rb(AppConstants.COUNTRY_ID.toString())
        params["category_id"] = rb(AppConstants.getCategoryID.toString())
        params["brand_id"] = rb(AppConstants.getBrandID.toString())
        params["state"] = rb(binding.spinnerState.selectedItemPosition.toString())
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

    @SuppressLint("UseCompatLoadingForDrawables", "CommitTransaction", "SetTextI18n")
    @Suppress("DEPRECATION")
    private fun uploadProductElectronic(someInt: Int) {


        viewModel.getCategoryID("Bearer ${AppConstants.TOKEN_USER}", someInt)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 50


        val layoutTextTITLE = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val layoutEdit = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        viewModel.myGetCategoryID.observe(viewLifecycleOwner) { list ->

            binding.inElectron.removeAllViews()
            ed.clear()

            try {


                for (i in 0 until list.body()?.data?.fields?.size!!.toInt()) {



                    val linearLayout2 = LinearLayout(binding.inElectron.context)
                    linearLayout2.removeAllViews()
                    linearLayout2.setPadding(15, 10, 15, 10)
                    linearLayout2.background = context?.resources?.getDrawable(R.drawable.button_background2)
                    linearLayout2.orientation = LinearLayout.VERTICAL
                    linearLayout2.layoutParams = layoutParams

                    val textView = TextView(linearLayout2.context)
                    textView.textSize = 16f

                    if (
                        list.body()?.data?.fields?.get(i)?.type == "string"
                    ) {

                        textView.text = list.body()?.data?.fields?.get(i)?.name
                        textView.layoutParams = layoutTextTITLE
                        val maxLenght = 200

                        val editText = EditText(linearLayout2.context)
                        editText.layoutParams = layoutEdit
                        editText.inputType = EditorInfo.TYPE_CLASS_TEXT
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLenght))
                        editText.background.mutate()
                            .setColorFilter(
                                resources.getColor(R.color.fon1),
                                PorterDuff.Mode.SRC_ATOP
                            )

                        val text = TextView(linearLayout2.context)
                        text.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginStart = 10
                        }
                        editText.doOnTextChanged { _, _, _, _ ->
                            text.text = "Длина: ${editText.length()}"
                        }

                        text.text = "Длина: ${editText.length()}"


                        linearLayout2.addView(textView)
                        linearLayout2.addView(editText)
                        linearLayout2.addView(text)

                        binding.inElectron.addView(linearLayout2)
                        ed.add(editText)

                    }
                    if (list.body()?.data?.fields?.get(i)?.type == "number") {


                        textView.text = list.body()?.data?.fields?.get(i)?.name
                        textView.layoutParams = layoutTextTITLE
                        val minValueEditText = list.body()?.data?.fields?.get(i)?.min!!
                        val maxValueEditText = list.body()?.data?.fields?.get(i)?.max!!

                        val editText = EditText(linearLayout2.context)
                        editText.layoutParams = layoutEdit
                        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
                        editText.background.mutate()
                            .setColorFilter(
                                resources.getColor(R.color.fon1),
                                PorterDuff.Mode.SRC_ATOP
                            )

                        editText.filters = arrayOf<InputFilter>(
                            InputFilter { source, _, _, dest, _, _ ->
                                try {
                                    val input = (dest.toString() + source.toString()).toInt()
                                    if (isInRange(1, maxValueEditText, input)) {
                                        null
                                    } else {
                                        ""
                                    }
                                } catch (nfe: NumberFormatException) {
                                    ""
                                }
                            }
                        )

                        val text = TextView(linearLayout2.context)
                        text.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginStart = 10
                        }

                        text.text = "Ввидите число от: $minValueEditText   до: $maxValueEditText"

                        linearLayout2.addView(textView)
                        linearLayout2.addView(editText)
                        linearLayout2.addView(text)
                        binding.inElectron.addView(linearLayout2)
                        ed.add(editText)
                    }

                    if (list.body()?.data?.fields?.get(i)?.type == "rate") {

                        textView.text = list.body()?.data?.fields?.get(i)?.name
                        textView.layoutParams = layoutTextTITLE

                        // создаем новый LinearLayout
                        val linearLayout = LinearLayout(context)
                        // устанавливаем ориентацию горизонтально
                        linearLayout.orientation = LinearLayout.HORIZONTAL

                        val minValue = list.body()?.data?.fields?.get(i)!!.min?.toFloat()!!
                        val maxValue = list.body()?.data?.fields?.get(i)!!.max?.toFloat()!!


                        val slider = Slider(binding.inElectron.context)
                        slider.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        slider.valueFrom = minValue
                        slider.valueTo = maxValue
                        slider.value = minValue
                        slider.labelBehavior = LabelFormatter.LABEL_GONE



                        val textTitle = TextView(linearLayout2.context)
                        textTitle.textSize = 13f
                        textTitle.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textTitle.text = "Значение: "


                        val editTextSlider = EditText(linearLayout2.context)
                        editTextSlider.layoutParams = LinearLayout.LayoutParams(
                            250,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        editTextSlider.inputType = EditorInfo.TYPE_CLASS_NUMBER
                        editTextSlider.background.mutate()
                            .setColorFilter(
                                resources.getColor(R.color.fon1),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        editTextSlider.filters = arrayOf<InputFilter>(
                            InputFilter { source, _, _, dest, _, _ ->
                                try {
                                    val input = (dest.toString() + source.toString()).toInt()
                                    if (isInRange(1, maxValue.toInt(), input)) {
                                        null
                                    } else {
                                        ""
                                    }
                                } catch (nfe: NumberFormatException) {
                                    ""
                                }
                            }
                        )


                        val text = TextView(linearLayout2.context)
                        text.textSize = 12f
                        text.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL
                        }
                        text.text = "от: ${minValue.toInt()} -- до: ${maxValue.toInt()}"

                        slider.addOnChangeListener { _, value, _ ->
                            text.text = ""
                            editTextSlider.setText(value.toInt().toString())
                            editTextSlider.setSelection(editTextSlider.text.length)
                        }


                        editTextSlider.doOnTextChanged { txt, _, _, _ ->
                            if (editTextSlider.text.isNotEmpty()){
                                if (editTextSlider.text.toString().toInt() >= minValue.toInt()){
                                    slider.value = txt.toString().toFloat()
                                }
                            }else{
                                text.text = ("от: ${minValue.toInt()} -- до: ${maxValue.toInt()}")
                                slider.value = minValue
                            }
                        }




                        linearLayout2.addView(textTitle)
                        linearLayout.addView(editTextSlider)
                        linearLayout.addView(text)
                        linearLayout2.addView(textView)
                        linearLayout2.addView(linearLayout)
                        linearLayout2.addView(slider)

                        binding.inElectron.addView(linearLayout2)
                        ed.add(slider)

                    }

                    if (list.body()?.data?.fields?.get(i)?.type == "select") {

                        textView.text = list.body()?.data?.fields?.get(i)?.name
                        textView.layoutParams = layoutEdit

                        val spinner = Spinner(linearLayout2.context)
                        spinner.layoutParams = layoutTextTITLE


                        val arrayAdapterCity = ArrayAdapter(
                            requireContext(), R.layout.item_dropdown,
                            list.body()?.data?.fields?.get(i)?.value as ArrayList
                        )


                        spinner.adapter = arrayAdapterCity

                        linearLayout2.addView(textView)
                        linearLayout2.addView(spinner)

                        binding.inElectron.addView(linearLayout2)
                        ed.add(spinner)

                    }
                }

            } catch (e: NullPointerException) {
                Toast.makeText(requireContext(), "Дополнительные поля пусто", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return value in min..max
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
        outState.putInt("CA_INDEX", caIndex!!)

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
        if (requestCode == 50 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            if (result != null) {
                binding.textNewProductCategory.setText(result)
                uploadProductElectronic(AppConstants.getCategoryID)
            }
        } else if (requestCode == 63 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("textBrand2")
            if (result != null) {
                binding.textNewProductBrand.setText(result)
            }
        }else if (requestCode == 88 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("textCountryName")
            if (result != null) {
                binding.edtNameCity.setText(result)
            }
        }else if (requestCode == 302 && resultCode == Activity.RESULT_OK && data != null) {
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
            binding.ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            MyUtils.uGlide(requireContext(), binding.ivImage, uri)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )
            stateSelectImageFirst = true
        } catch (e: Exception) {
            e.printStackTrace()
            MyUtils.uGlide(requireContext(), binding.ivImage, R.drawable.img_select)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}