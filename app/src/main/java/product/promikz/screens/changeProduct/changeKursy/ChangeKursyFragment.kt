package product.promikz.screens.changeProduct.changeKursy

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.ButtonGravity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.R
import product.promikz.databinding.FragmentChangeKursyBinding
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.screens.create.newProduct.ProductCreateAdapter
import product.promikz.screens.create.newProduct.category.CategoryChangeFragment
import product.promikz.viewModels.HomeViewModel
import java.io.File

class ChangeKursyFragment : Fragment() {

    lateinit var dialog: Dialog

    private lateinit var binding: FragmentChangeKursyBinding

    private lateinit var mChangeProUpdate: HomeViewModel

    private var idProducts: Int = -1
    private var selectedDelete: Boolean = false

    private lateinit var recyclerViewCreate: RecyclerView
    lateinit var adapterProduct: ProductCreateAdapter

    private var selectedUriList = ArrayList<Uri>()
    private var globalSelectedUriList2 = ArrayList<Uri>()
    private var selectedUrlAddress = ArrayList<Uri>()

    private val installment = HashMap<String, RequestBody>()

    private var uri: Uri? = null

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

        binding = FragmentChangeKursyBinding.inflate(inflater, container, false)


        if (savedInstanceState != null) {

            nameSave = savedInstanceState.getString("NAME_SAVE")
            uri = savedInstanceState.getParcelable("LIST")!!
            globalSelectedUriList2 = savedInstanceState.getParcelableArrayList("All_LIST")!!
            descriptionSave = savedInstanceState.getString("DESCRIPTION_SAVE")
            priceSave = savedInstanceState.getInt("PRICE_SAVE")

        }


        dialog = Dialog(requireContext())



        val firsRas = resources.getStringArray(R.array.RasFirst)
        val arrayAdapterFirsRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, firsRas)
        binding.spinnerFirsRas.adapter = arrayAdapterFirsRas

        val monthRas = resources.getStringArray(R.array.RasMonth)
        val arrayAdapterMonthRas = ArrayAdapter(requireContext(), R.layout.item_dropdown, monthRas)
        binding.spinnerMonthRas.adapter = arrayAdapterMonthRas

        val arguments = (activity as AppCompatActivity).intent.extras
        idProducts = arguments!!["changeProductID"] as Int

        recyclerViewCreate = binding.rvCreate
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

        setNormalSingleButton()
        setNormalMultiButton()



        binding.edtNameCity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(CategoryChangeFragment(), "get")
                .commit()
        }



        mChangeProUpdate.getUpdateDataArrayUpdate("Bearer ${AppConstants.TOKEN_USER}", idProducts)
        mChangeProUpdate.myShowProducts.observe(viewLifecycleOwner) { list ->

            if (nameSave == null) {
                binding.textNewProductName.setText(list.body()?.data?.name)
            } else {
                binding.textNewProductName.setText(nameSave)
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


            Glide.with(requireContext()).load(list.body()!!.data.img)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .centerCrop()
                .into(binding.ivImage)




            if (descriptionSave == null) {
                binding.textNewProductDescription.setText(list.body()?.data?.description)
            } else {
                binding.textNewProductDescription.setText(descriptionSave)
            }

            if (list.body()?.data?.review == 1) {
                binding.swichReview.isChecked = true
            }

            getLoadCity(list.body()?.data?.city?.id!!)


        }

        mChangeProUpdate.myUpdateKursy.observe(viewLifecycleOwner) { list ->


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



            if (binding.textNewProductName.length() != 0 && binding.textNewProductDescription.length() != 0

            ) {
                uploadProduct()

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

        if (globalSelectedUriList2.isNotEmpty()) {
            showMultiImage(globalSelectedUriList2)
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

    private fun getLoadCity(id: Int) {
        mChangeProUpdate.getCity(id)
        mChangeProUpdate.myCity.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                binding.edtNameCity.setText(list.body()?.data?.name.toString())
                AppConstants.COUNTRY_ID = list.body()?.data?.id
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
        }else {
            installment["review"] = rb("0")
        }


        mChangeProUpdate.pushUpdateKursy(
            "Bearer ${AppConstants.TOKEN_USER}",
            idProducts,
            rb(put),
            rb("2"),
            rb(binding.textNewProductName.text.toString().trim()),
            rb(AppConstants.COUNTRY_ID.toString()),
            if (binding.textNewProductPrice.length() != 0) {
                rb(binding.textNewProductPrice.text.toString().trim())
            } else {
                rb("0")
            },
            filePart,
            rb(binding.textNewProductDescription.text.toString().trim()),
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
            AppConstants.isStatusMultiImages = true
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
    private fun showSingleImage(uri: Uri) {
        try {
            binding.ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(requireContext()).load(uri)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .centerCrop()
                .into(binding.ivImage)
            val f = File.createTempFile("suffix", "prefix", requireContext().cacheDir)
            f.outputStream()
                .use {
                    requireContext().contentResolver.openInputStream(uri)
                        ?.copyTo(it)
                }

            val requestBody = f.asRequestBody("image/*".toMediaTypeOrNull())

            filePart = MultipartBody.Part.createFormData("img", f.name, requestBody)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("NAME_SAVE", nameSave)
        outState.putParcelable("LIST", uri)
        outState.putParcelableArrayList("All_LIST", globalSelectedUriList2)
        outState.putString("DESCRIPTION_SAVE", descriptionSave)
        outState.putInt("PRICE_SAVE", priceSave!!)

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
}