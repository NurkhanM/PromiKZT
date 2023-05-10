package product.promikz.screens.changeProduct

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.isStatusMultiImages
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.R
import product.promikz.databinding.FragmentChangeProductBinding
import product.promikz.screens.create.newProduct.ProductCreateAdapter
import product.promikz.viewModels.HomeViewModel
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.ButtonGravity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import product.promikz.AppConstants.COUNTRY_ID
import product.promikz.AppConstants.getBrandID
import product.promikz.AppConstants.getCategoryID
import product.promikz.MyUtils
import product.promikz.MyUtils.uToast
import product.promikz.screens.create.newProduct.brand.BrandProductChangeFragment
import product.promikz.screens.create.newProduct.category.CategoryChangeFragment
import product.promikz.screens.create.newProduct.country.CounterSelectFragment
import java.io.ByteArrayOutputStream
import java.io.File

class ChangeProductFragment : Fragment() {


    lateinit var dialog: Dialog

    private lateinit var binding: FragmentChangeProductBinding

    private lateinit var mChangeProUpdate: HomeViewModel

    private var idProducts: Int = -1
    private var selectedDelete: Boolean = false

    private lateinit var recyclerViewCreate: RecyclerView
    lateinit var adapterProduct: ProductCreateAdapter

    private var selectedUriList = ArrayList<Uri>()
    private var selectedUrlAddress = ArrayList<Uri>()

    private val installment = HashMap<String, RequestBody>()

    private val map = HashMap<String, RequestBody>()

    private val ed = ArrayList<View>()
    private val ed2 = ArrayList<View>()
    private val uploadED = ArrayList<View>()
    private val uploadED2 = ArrayList<View>()
    private val edPivotID = ArrayList<Int>()


    private var start = false
    private var isHarakter: Boolean = true
    private var nameSave: String? = null
    private var priceSave: Int? = -1
    private var descriptionSave: String? = null
    private var filePart: MultipartBody.Part? = null
    private var filePart2 = ArrayList<MultipartBody.Part>()

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "CommitTransaction")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        mChangeProUpdate = ViewModelProvider(this)[HomeViewModel::class.java]
        COUNTRY_ID = 0
        getCategoryID = -1
        getBrandID = -1

        dialog = Dialog(requireContext())

        binding = FragmentChangeProductBinding.inflate(inflater, container, false)


        val state = resources.getStringArray(R.array.State)
        val arrayAdapterState = ArrayAdapter(requireContext(), R.layout.item_dropdown, state)
        binding.changeProSpinnerState.adapter = arrayAdapterState

        val firsRas = resources.getStringArray(R.array.RasFirst)
        val arrayAdapterFirsRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, firsRas)
        binding.spinnerFirsRas.adapter = arrayAdapterFirsRas

        val monthRas = resources.getStringArray(R.array.RasMonth)
        val arrayAdapterMonthRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, monthRas)
        binding.spinnerMonthRas.adapter = arrayAdapterMonthRas

        val arguments = (activity as AppCompatActivity).intent.extras
        idProducts = arguments!!["changeProductID"] as Int

        recyclerViewCreate = binding.rvChangePro
        adapterProduct = ProductCreateAdapter(object : IClickListnearUpdateImage {
            override fun clickListener(baseID: String, index: Int) {
                if (selectedDelete) {
                    if (filePart2.size <= 1) {
                        Toast.makeText(
                            requireContext(),
                            resources.getText(R.string.need_one_picture),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        filePart2.removeAt(index)
                        adapterProduct.deleteMyEducations(index)
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.upload_pictures),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }


            }

        })
        recyclerViewCreate.adapter = adapterProduct
        recyclerViewCreate.setHasFixedSize(true)

        recyclerViewCreate.setHasFixedSize(true)
        binding.changeProIvImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 303)
        }

        setNormalMultiButton()





        binding.edtNameCity.setOnClickListener {

            val fragment = CounterSelectFragment()
            fragment.setTargetFragment(this, 60)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()
        }




        binding.textNewChangeProCategory.setOnClickListener {
            val fragment = CategoryChangeFragment()
            fragment.setTargetFragment(this, 50)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()
        }




        binding.textNewChangeProBrand.setOnClickListener {

            val fragment = BrandProductChangeFragment()
            fragment.setTargetFragment(this, 61)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()


        }






        mChangeProUpdate.getUpdateDataArrayUpdate("Bearer $TOKEN_USER", idProducts)
        mChangeProUpdate.myShowProducts.observe(viewLifecycleOwner) { list ->

            if (nameSave == null) {
                binding.textNewChangeProName.setText(list.body()?.data?.name)
            } else {
                binding.textNewChangeProName.setText(nameSave)
            }


            for (i in 0 until list.body()!!.data.images.size) {
                selectedUrlAddress.add(list.body()!!.data.images[i].name.toUri())
            }

            adapterProduct.setList(selectedUrlAddress)


            if (list.body()?.data?.installment != null) {
                binding.swichRassrochka.isChecked = true
                binding.edtSumRas.setText(list.body()?.data?.installment?.price.toString())
                binding.spinnerFirsRas.setSelection(getFirstRas(list.body()?.data?.installment?.firstPercent.toString()))
                binding.spinnerMonthRas.setSelection(getMonthRas(list.body()?.data?.installment?.type.toString()))
                binding.vRassrochku2.visibility = View.VISIBLE
            }

            if (list.body()?.data?.review == 1) {
                binding.swichReview.isChecked = true
            }



            MyUtils.uGlide(requireContext(), binding.changeProIvImage, list.body()!!.data.img)

            if (descriptionSave == null) {
                binding.textNewChangeProDescription.setText(list.body()?.data?.description)
            } else {
                binding.textNewChangeProDescription.setText(descriptionSave)
            }


            list.body()?.data?.brand?.let {
                binding.textNewChangeProBrand.setText(it.name)
                getBrandID = it.id
            }

            list.body()?.data?.categories?.let {
                binding.textNewChangeProCategory.setText(it[0].name)
                getCategoryID = it[0].id
            }


            if (priceSave == -1) {

                list.body()?.data?.let {
                    if (it.price.toString() != "0") {
                        binding.textNewChangeProPrice.setText(it.price.toString())
                    } else {
                        binding.textNewChangeProPrice.setText("")
                    }
                }
            } else {
                binding.textNewChangeProPrice.setText(priceSave!!.toString())
            }

            binding.changeProSpinnerState.setSelection(list.body()!!.data.state)

            if (list.body()?.data?.show_fields == true){
                binding.swichFields.isChecked = true
            }


//            mChangeProUpdate.getCategoryID(
//                "Bearer $TOKEN_USER",
//                list.body()?.data?.categories?.get(0)?.id!!
//            )

//            mChangeProUpdate.getUpdateDataArrayUpdate(
//                "Bearer $TOKEN_USER",
//                idProducts
//            )


            fieldsUpload()
            getLoadCity(list.body()?.data?.city?.id!!)


        }

        mChangeProUpdate.myUpdateCreate.observe(viewLifecycleOwner) { list ->


            if (list.isSuccessful) {
                withStyle()
            } else {
                binding.createLoader.visibility = View.GONE
                binding.changeProClickUpdateBackCard.visibility = View.VISIBLE
                binding.fonCreate.visibility = View.VISIBLE
                binding.textTitle.text = resources.getString(R.string.edit)
                try {


                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))
                    val messageArray = ArrayList<String>()

                    for (name in jsonObjError.keys()){

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()){
                            messageArray.add(nameArray.getString(i))
                            messageArray.add("\n\n")

                        }

                    }

                    alertDialogCancel(messageArray.joinToString().replace(",", ""))

                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error Server", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.postChangeProBTN.setOnClickListener {

            binding.createLoader.visibility = View.VISIBLE
            binding.textTitle.text = resources.getString(R.string.wait)
            binding.changeProClickUpdateBackCard.visibility = View.GONE
            binding.fonCreate.visibility = View.GONE

            if (ed.isNotEmpty()) {
                if (binding.textNewChangeProName.length() != 0 && binding.textNewChangeProDescription.length() != 0 &&
                    binding.changeProSpinnerState.selectedItem != null
                ) {


                    ed2.forEachIndexed { index, view ->
                        when (view){
                            is EditText -> {
                                if (view.text.isNotEmpty()){
                                    map["fields[${edPivotID[index]}]"] = rb(rbView(ed2[index]))
                                }
                            }

                            else -> {
                                map["fields[${edPivotID[index]}]"] = rb(rbView(ed2[index]))
                            }
                        }

                    }

                    mChangeProUpdate.myGetCategoryEnd.observe(viewLifecycleOwner) { li ->

                        val sizeRequired = ArrayList<Int>()
                        li.body()?.data?.fields?.forEach { countR ->
                            if (countR.required == 1) {
                                sizeRequired.add(countR.pivot_id)
                            }
                        }

                        for (i in 0 until sizeRequired.size) {
                            map["fields[${sizeRequired[i]}]"] = rb(rbView(ed[i]))
                        }



                        uploadProduct()

                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.add_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.createLoader.visibility = View.GONE
                    binding.changeProClickUpdateBackCard.visibility = View.VISIBLE
                    binding.fonCreate.visibility = View.VISIBLE
                    binding.textTitle.text = resources.getString(R.string.create_ads)

                }
            } else {

                uploadED2.forEachIndexed { index, view ->

                    when (view){
                        is EditText -> {
                            if (view.text.isNotEmpty()){
                                map["fields[${edPivotID[index]}]"] = rb(rbView(uploadED2[index]))
                            }
                        }

                        else -> {
                            map["fields[${edPivotID[index]}]"] = rb(rbView(uploadED2[index]))
                        }
                    }

                }


                mChangeProUpdate.myShowProducts.observe(viewLifecycleOwner) { li ->

                    val sizeRequired = ArrayList<Int>()
                    li.body()?.data?.fields?.forEach { countR ->
                        if (countR.required == 1) {
                            sizeRequired.add(countR.pivot_id)
                        }
                    }

                    for (i in 0 until sizeRequired.size) {
                        map["fields[${sizeRequired[i]}]"] = rb(rbView(uploadED[i]))
                    }

                }

                uploadProduct()
            }

        }
        binding.textNewChangeProName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                v.clearFocus()
                closeKeyBoard(v)
                return@OnKeyListener true
            }
            false
        })


        binding.changeProClickUpdateBackCard.setOnClickListener {
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


        binding.showFiedls.setOnClickListener {
            if (isHarakter) {
                isHarakter = false
                binding.imgHarakter.setImageResource(R.drawable.bottom_right)
                binding.inChangeProElectron.visibility = View.GONE
                binding.fieldsUpload.visibility = View.GONE
            } else {
                isHarakter = true
                binding.imgHarakter.setImageResource(R.drawable.bottom)
                binding.inChangeProElectron.visibility = View.VISIBLE
                binding.fieldsUpload.visibility = View.VISIBLE
            }
        }


        return binding.root


    }

    private fun getLoadCity(id: Int) {
        mChangeProUpdate.getCity(id)
        mChangeProUpdate.myCity.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                binding.edtNameCity.setText(list.body()?.data?.name.toString())
                COUNTRY_ID = list.body()?.data?.id
            } else {
                binding.edtNameCity.setText("")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val downloadedFile = File(Environment.getExternalStorageDirectory(), "Pictures/pikachu")
        downloadedFile.deleteRecursively()
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
        val put = "put"

        if (binding.swichRassrochka.isChecked && binding.edtSumRas.text.isNotEmpty()) {

            installment["installment[success]"] = rb("1")
            installment["installment[price]"] = rb(binding.edtSumRas.text.toString())
            installment["installment[firstPercent]"] = rb(firstPercent())
            installment["installment[type]"] = rb(instalmentType())

        } else {
            installment["installment[success]"] = rb("0")
        }

        if (binding.swichReview.isChecked) {
            installment["review"] = rb("1")
        }

        if (binding.swichFields.isChecked) {
            installment["show_fields"] = rb("1")
        } else {
            installment["show_fields"] = rb("0")
        }


        mChangeProUpdate.pushUpdateCreate(
            "Bearer $TOKEN_USER",
            idProducts,
            rb(put),
            rb(binding.textNewChangeProName.text.toString().trim()),
            rb(COUNTRY_ID.toString()),
            rb(getCategoryID.toString()),
            rb(getBrandID.toString()),
            rb(binding.changeProSpinnerState.selectedItemPosition.toString()),
            if (binding.textNewChangeProPrice.length() != 0) {
                rb(binding.textNewChangeProPrice.text.toString().trim())
            } else {
                rb("0")
            },
            filePart,
            rb(binding.textNewChangeProDescription.text.toString().trim()),
            map,
            filePart2,
            installment
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

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    private fun uploadProductElectronic() {


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


        binding.newProgress.visibility = View.VISIBLE

        mChangeProUpdate.myGetCategoryEnd.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding.newProgress.visibility = View.GONE
                binding.inChangeProElectron.removeAllViews()
                ed.clear()
                ed2.clear()
                edPivotID.clear()

                for (i in 0 until list.body()?.data?.fields?.size!!.toInt()) {

                    val linearLayout2 = LinearLayout(binding.inChangeProElectron.context)
                    linearLayout2.removeAllViews()
                    linearLayout2.setPadding(15, 10, 15, 10)
                    linearLayout2.background =
                        context?.resources?.getDrawable(R.drawable.button_background2)
                    linearLayout2.orientation = LinearLayout.VERTICAL
                    linearLayout2.layoutParams = layoutParams

                    val textView = TextView(linearLayout2.context)
                    textView.textSize = 16f



                    if (
                        list.body()?.data?.fields?.get(i)?.type == "string"
                    ) {

                        textView.text = list.body()?.data?.fields?.get(i)?.name
                        textView.layoutParams = layoutTextTITLE
                        val maxLenght = list.body()?.data?.fields?.get(i)?.max!!
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

                        binding.inChangeProElectron.addView(linearLayout2)
                        if (list.body()?.data?.fields?.get(i)?.required == 1){
                            val textSpan = list.body()?.data?.fields?.get(i)?.name + " *"
                            val spannableString = SpannableString(textSpan)

                            val startIndex = textSpan.indexOf("*")
                            val endIndex = startIndex + 1

                            val colorSpan = ForegroundColorSpan(Color.RED)
                            spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textView.text = spannableString
                            ed.add(editText)
                        }else{
                            ed.add(editText)
                            edPivotID.add(list.body()?.data?.fields?.get(i)?.pivot_id!!)
                        }

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

                        binding.inChangeProElectron.addView(linearLayout2)
                        if (list.body()?.data?.fields?.get(i)?.required == 1){
                            val textSpan = list.body()?.data?.fields?.get(i)?.name + " *"
                            val spannableString = SpannableString(textSpan)

                            val startIndex = textSpan.indexOf("*")
                            val endIndex = startIndex + 1

                            val colorSpan = ForegroundColorSpan(Color.RED)
                            spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textView.text = spannableString
                            ed.add(editText)
                        }else{
                            ed.add(editText)
                            edPivotID.add(list.body()?.data?.fields?.get(i)?.pivot_id!!)
                        }
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


                        val slider = Slider(binding.inChangeProElectron.context)
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
                            200,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        editTextSlider.hint = "..."

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
                        }


                        editTextSlider.doOnTextChanged { txt, _, _, _ ->
                            if (editTextSlider.text.isNotEmpty()) {
                                if (editTextSlider.text.toString().toInt() >= minValue.toInt()) {
                                    slider.value = txt.toString().toFloat()
                                }
                            } else {
                                text.text = ("от: ${minValue.toInt()} -- до: ${maxValue.toInt()}")
                            }
                        }




                        linearLayout2.addView(textView)
                        linearLayout.addView(textTitle)
                        linearLayout.addView(editTextSlider)
                        linearLayout.addView(text)
                        linearLayout2.addView(linearLayout)
                        linearLayout2.addView(slider)

                        binding.inChangeProElectron.addView(linearLayout2)
                        if (list.body()?.data?.fields?.get(i)?.required == 1){
                            val textSpan = list.body()?.data?.fields?.get(i)?.name + " *"
                            val spannableString = SpannableString(textSpan)

                            val startIndex = textSpan.indexOf("*")
                            val endIndex = startIndex + 1

                            val colorSpan = ForegroundColorSpan(Color.RED)
                            spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textView.text = spannableString
                            ed.add(slider)
                        }else{
                            ed.add(slider)
                            edPivotID.add(list.body()?.data?.fields?.get(i)?.pivot_id!!)
                        }
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

                        binding.inChangeProElectron.addView(linearLayout2)
                        if (list.body()?.data?.fields?.get(i)?.required == 1){
                            val textSpan = list.body()?.data?.fields?.get(i)?.name + " *"
                            val spannableString = SpannableString(textSpan)

                            val startIndex = textSpan.indexOf("*")
                            val endIndex = startIndex + 1

                            val colorSpan = ForegroundColorSpan(Color.RED)
                            spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textView.text = spannableString
                            ed.add(spinner)
                        }else{
                            ed.add(spinner)
                            edPivotID.add(list.body()?.data?.fields?.get(i)?.pivot_id!!)
                        }

                    }
                }
            } else {
                Toast.makeText(requireContext(), "gone", Toast.LENGTH_SHORT).show()
            }
        }

//        mChangeProUpdate.myGetCategoryID.observe(viewLifecycleOwner) { li ->
//            for (i in 0 until li.body()?.data?.fields?.size!!.toInt()) {
//                map["fields[${li.body()?.data?.fields?.get(i)?.pivot_id.toString()}]"] =
//                    rb(rbView(ed[i]))
//            }
//        }

    }


    private fun fieldsStatus(arr: ArrayList<View>): Boolean {

        var state = false
        val arrayMas = ArrayList<Boolean>()

        for (i in 0 until arr.size) {
            if (arr[i] is EditText) {
                if ((arr[i] as EditText).length() != 0) {
                    arrayMas.add(true)
                } else {
                    arrayMas.add(false)
                }
            }
        }

        for (i in 0 until arrayMas.size) {
            state = arrayMas[i]
        }

        return state
    }

    private fun closeKeyBoard(view: View) {
        val imm: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setNormalMultiButton() {
        binding.changeProBtnNormalMulti.setOnClickListener {
            isStatusMultiImages = true
            TedImagePicker.with(requireContext())
                .max(10, resources.getText(R.string.maximum_10).toString())
                .mediaType(gun0912.tedimagepicker.builder.type.MediaType.IMAGE)
                .buttonGravity(ButtonGravity.BOTTOM)
                .errorListener { message -> Log.d("ted", "message: $message") }
                .selectedUri(selectedUriList)
                .startMultiImage { list: List<Uri> ->
                    selectedUriList.clear()
                    selectedUriList.addAll(list)
                    selectedDelete = true
                    showMultiImage(selectedUriList)
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
                        requireContext().contentResolver.openInputStream(uriList[i])
                            ?.copyTo(it)
                        val requestBody = f.asRequestBody("image/*".toMediaTypeOrNull())
                        list.add(MultipartBody.Part.createFormData("images[]", f.name, requestBody))
                    }
            }
            filePart2.clear()
            filePart2.addAll(list)

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

    @Suppress("DEPRECATION")
    private fun withStyle() {

        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        alertDialogBuilder.setTitle(R.string.successfully)
        alertDialogBuilder.setMessage(R.string.successfully_change_product)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setNegativeButton(
            resources.getText(R.string.continue_app)
        ) { _, _ ->
            activity?.onBackPressed()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @Suppress("DEPRECATION")
    private fun fieldsUpload() {


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

        mChangeProUpdate.myShowProducts.observe(viewLifecycleOwner) { res ->

            binding.fieldsUpload.removeAllViews()
            uploadED.clear()
            uploadED2.clear()
            edPivotID.clear()

            for (i in 0 until res.body()?.data?.fields?.size!!.toInt()) {


                val linearLayout2 = LinearLayout(binding.fieldsUpload.context)
                linearLayout2.removeAllViews()
                linearLayout2.setPadding(15, 10, 15, 10)
                linearLayout2.background =
                    context?.resources?.getDrawable(R.drawable.button_background2)
                linearLayout2.orientation = LinearLayout.VERTICAL
                linearLayout2.layoutParams = layoutParams

                val textView = TextView(linearLayout2.context)
                textView.textSize = 16f



                if (res.body()?.data?.fields?.get(i)?.type == "string") {

                    textView.text = res.body()?.data?.fields?.get(i)?.name
                    textView.layoutParams = layoutTextTITLE
                    val maxLenght = res.body()?.data?.fields?.get(i)?.max?.toInt()!!

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

                    res.body()?.data?.fields?.get(i)?.valueUser?.let { value ->
                        editText.setText(value)
                    }
                    text.text = "Длина: ${editText.length()}"


                    linearLayout2.addView(textView)
                    linearLayout2.addView(editText)
                    linearLayout2.addView(text)
                    binding.fieldsUpload.addView(linearLayout2)
                    if (res.body()?.data?.fields?.get(i)?.required == 1){
                        val textSpan = res.body()?.data?.fields?.get(i)?.name + " *"
                        val spannableString = SpannableString(textSpan)

                        val startIndex = textSpan.indexOf("*")
                        val endIndex = startIndex + 1

                        val colorSpan = ForegroundColorSpan(Color.RED)
                        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        textView.text = spannableString
                        uploadED.add(editText)
                    }else{
                        uploadED2.add(editText)
                        edPivotID.add(res.body()?.data?.fields?.get(i)?.pivot_id!!)
                    }


                }
                if (res.body()?.data?.fields?.get(i)?.type == "number") {

                    textView.text = res.body()?.data?.fields?.get(i)?.name
                    textView.layoutParams = layoutTextTITLE


                    val minValueEditText = res.body()?.data?.fields?.get(i)?.min?.toInt()!!
                    val maxValueEditText = res.body()?.data?.fields?.get(i)?.max?.toInt()!!


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

                    res.body()?.data?.fields?.get(i)?.valueUser?.let { value ->
                        editText.setText(value)
                    }

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
                    binding.fieldsUpload.addView(linearLayout2)
                    if (res.body()?.data?.fields?.get(i)?.required == 1){
                        val textSpan = res.body()?.data?.fields?.get(i)?.name + " *"
                        val spannableString = SpannableString(textSpan)

                        val startIndex = textSpan.indexOf("*")
                        val endIndex = startIndex + 1

                        val colorSpan = ForegroundColorSpan(Color.RED)
                        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        textView.text = spannableString
                        uploadED.add(editText)
                    }else{
                        uploadED2.add(editText)
                        edPivotID.add(res.body()?.data?.fields?.get(i)?.pivot_id!!)
                    }

                }

                if (res.body()?.data?.fields?.get(i)?.type == "rate") {

                    textView.text = res.body()?.data?.fields?.get(i)?.name
                    textView.layoutParams = layoutTextTITLE

                    // создаем новый LinearLayout
                    val linearLayout = LinearLayout(context)
                    // устанавливаем ориентацию горизонтально
                    linearLayout.orientation = LinearLayout.HORIZONTAL

                    val minValue = res.body()?.data?.fields?.get(i)?.min?.toFloat()!!
                    val maxValue = res.body()?.data?.fields?.get(i)?.max?.toFloat()!!


                    val slider = Slider(linearLayout2.context)
                    slider.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    slider.valueFrom = minValue
                    slider.valueTo = maxValue
                    slider.value = minValue
                    slider.stepSize = 1.0f
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
                        200,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    editTextSlider.hint = "..."

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
                        val progress = value.toInt()
                        text.text = ""
                        editTextSlider.setText("$progress")
                        editTextSlider.setSelection(editTextSlider.text.length)

                    }


                    editTextSlider.doOnTextChanged { txt, _, _, _ ->
                        if (editTextSlider.text.isNotEmpty()) {
                            if (editTextSlider.text.toString().toInt() >= minValue.toInt()) {
                                slider.value = txt.toString().toFloat()
                            }
                        } else {
                            text.text = ("от: ${minValue.toInt()} -- до: ${maxValue.toInt()}")
                            slider.value = minValue
                        }
                    }

                    res.body()?.data?.fields?.get(i)?.valueUser?.let { value ->
                        editTextSlider.setText(value)
                    }


                    linearLayout2.addView(textView)
                    linearLayout.addView(textTitle)
                    linearLayout.addView(editTextSlider)
                    linearLayout.addView(text)
                    linearLayout2.addView(linearLayout)
                    linearLayout2.addView(slider)

                    binding.fieldsUpload.addView(linearLayout2)

                    if (res.body()?.data?.fields?.get(i)?.required == 1){
                        val textSpan = res.body()?.data?.fields?.get(i)?.name + " *"
                        val spannableString = SpannableString(textSpan)

                        val startIndex = textSpan.indexOf("*")
                        val endIndex = startIndex + 1

                        val colorSpan = ForegroundColorSpan(Color.RED)
                        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        textView.text = spannableString
                        uploadED.add(slider)
                    }
                    else{
                        uploadED2.add(slider)
                        edPivotID.add(res.body()?.data?.fields?.get(i)?.pivot_id!!)
                    }

                }



                if (res.body()?.data?.fields?.get(i)?.type == "select") {

                    textView.text = res.body()?.data?.fields?.get(i)?.name
                    textView.layoutParams = layoutEdit

                    val spinner = Spinner(linearLayout2.context)
                    spinner.layoutParams = layoutTextTITLE

//                    spinner.prompt = res.body()?.data?.fields?.get(i)?.valueUserNotRefact?.toInt()
//                        ?.let { res.body()?.data?.fields?.get(i)?.value?.get(it) }
//

                    val arrayAdapterCity = ArrayAdapter(
                        requireContext(), R.layout.item_dropdown,
                        res.body()?.data?.fields?.get(i)?.value!! as ArrayList
                    )
                    spinner.adapter = arrayAdapterCity


                    res.body()?.data?.fields?.get(i)?.valueUser?.let { value ->

                        res.body()?.data?.fields?.get(i)?.value!!.forEachIndexed { index, s ->
                            if (value == s) {
                                spinner.setSelection(index)
                            }
                        }

                    }


                    linearLayout2.addView(textView)
                    linearLayout2.addView(spinner)

                    binding.fieldsUpload.addView(linearLayout2)

                    if (res.body()?.data?.fields?.get(i)?.required == 1){
                        val textSpan = res.body()?.data?.fields?.get(i)?.name + " *"
                        val spannableString = SpannableString(textSpan)

                        val startIndex = textSpan.indexOf("*")
                        val endIndex = startIndex + 1

                        val colorSpan = ForegroundColorSpan(Color.RED)
                        spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        textView.text = spannableString
                        uploadED.add(spinner)
                    }
                    else{
                        uploadED2.add(spinner)
                        edPivotID.add(res.body()?.data?.fields?.get(i)?.pivot_id!!)
                    }
                }
            }
        }

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

//new

    private fun getFirstRas(str: String): Int {
        return when (str) {
            "5" -> 0
            "10" -> 1
            "15" -> 2
            "20" -> 3
            "30" -> 4
            "40" -> 5
            else -> 6
        }
    }

    private fun getMonthRas(str: String): Int {
        return when (str) {
            "3" -> 0
            "6" -> 1
            "12" -> 2
            "24" -> 3
            else -> 4
        }
    }

    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return value in min..max
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 50 && resultCode == Activity.RESULT_OK) {
            val resultCategory = data?.getStringExtra("textNews")
            if (resultCategory != null) {
                binding.fieldsUpload.visibility = View.GONE
                binding.inChangeProElectron.visibility = View.VISIBLE
                mChangeProUpdate.getCategoryIDEnd("Bearer $TOKEN_USER", getCategoryID)
                binding.textNewChangeProCategory.setText(resultCategory)
                uploadProductElectronic()
                ed.clear()
                start = true
            }
        } else if (requestCode == 60 && resultCode == Activity.RESULT_OK) {
            val resultCity = data?.getStringExtra("textCountryName")
            if (resultCity != null) {
                binding.edtNameCity.setText(resultCity)
            }
        } else if (requestCode == 61 && resultCode == Activity.RESULT_OK) {
            val resultBrand = data?.getStringExtra("textBrand")
            if (resultBrand != null) {
                binding.textNewChangeProBrand.setText(resultBrand)
            }
        } else if (requestCode == 303 && resultCode == Activity.RESULT_OK && data != null) {
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


    @Suppress("DEPRECATION")
    @SuppressLint("Recycle")
    private fun filePartScopMetod(uri: Uri) {
        try {
            binding.changeProIvImage.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(requireContext()).load(uri)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .into(binding.changeProIvImage)
            sendFileRequest(
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Glide.with(requireContext()).load(R.drawable.img_select)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .centerCrop()
                .into(binding.changeProIvImage)
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


}