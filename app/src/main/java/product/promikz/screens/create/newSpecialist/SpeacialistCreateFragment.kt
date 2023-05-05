package product.promikz.screens.create.newSpecialist

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.params
import product.promikz.AppConstants.params2
import product.promikz.AppConstants.params3
import product.promikz.AppConstants.specialistAllNumber
import product.promikz.AppConstants.specialistAllNumber2
import product.promikz.R
import product.promikz.screens.create.newSpecialist.selectedCategory.CategorySelectSpecialistFragment
import product.promikz.screens.create.newSpecialist.selectedCategory.SkillsSelectSpecialistFragment
import product.promikz.viewModels.HomeViewModel
import com.prongbang.dialog.SmartDialogFragment
import com.prongbang.dialog.SmartIcon
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import product.promikz.AppConstants
import product.promikz.AppConstants.MY_SPECIALIST
import product.promikz.AppConstants.getBrandID
import product.promikz.AppConstants.getCategoryID
import product.promikz.AppConstants.getSpecialistIDSTATE
import product.promikz.AppConstants.params4
import product.promikz.databinding.FragmentSpeacialistCreateBinding
import product.promikz.screens.create.newSpecialist.selectedCategory.SpecializationSelectSpecialistFragment
import tech.hibk.searchablespinnerlibrary.SearchableItem
import java.io.File

class SpeacialistCreateFragment : Fragment() {


    private var _binding: FragmentSpeacialistCreateBinding? = null
    private val binding get() = _binding!!


    private lateinit var mHomeViewModel: HomeViewModel
    lateinit var dialog: Dialog
    private var stateSelectImageFirst = false
    private var filePart: MultipartBody.Part? = null
    private var uri: Uri? = null
    private var countryIndex: Int? = 0
    private var cityIndex: Int? = 0
    private var justTime: Int? = 0

    @SuppressLint("CommitTransaction")
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentSpeacialistCreateBinding.inflate(inflater, container, false)
        val view = binding
        dialog = Dialog(requireContext())

        val departure = resources.getStringArray(R.array.Departure)
        val arrayAdapterState = ArrayAdapter(requireContext(), R.layout.item_dropdown, departure)
        view.createSpinDeparture.adapter = arrayAdapterState

        view.textInputLayoutPro3.setOnClickListener {
            specialistAllNumber.clear()
            view.createEdtSkills.text = ""

            val fragment = SkillsSelectSpecialistFragment()
            fragment.setTargetFragment(this, 82)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()
        }

        view.textInputLayoutPro4.setOnClickListener {
            specialistAllNumber2.clear()
            view.createEdtCategory.text = ""

            val fragment = CategorySelectSpecialistFragment()
            fragment.setTargetFragment(this, 83)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()

        }

        view.textInputLayoutSpecialization.setOnClickListener {
            view.createEdtSpecialization.text = ""

            val fragment = SpecializationSelectSpecialistFragment()
            fragment.setTargetFragment(this, 84)
            parentFragmentManager.beginTransaction()
                .add(fragment, fragment.tag)
                .commit()
        }

        view.postProductBTN.setOnClickListener {
            view.createLoader.visibility = View.VISIBLE
            view.textTitle.text = resources.getString(R.string.wait)
            view.clickUpdateBackCard.visibility = View.GONE
            view.fonCreate.visibility = View.GONE

            if (view.createEdtExperience.length() != 0 && countryIndex != 0 &&
                params2.size != 0 && params3.size != 0 && params4.size != 0
            ) {
                if (stateSelectImageFirst) {
                    uploadProduct()
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.selected_photo),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.add_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        setNormalSingleButton()


        view.textInputLayoutPro7.visibility = View.INVISIBLE
        mHomeViewModel.getCountry()
        mHomeViewModel.myCountry.observe(viewLifecycleOwner) { countryRES ->
            if (countryRES.isSuccessful) {
                val listCountry = arrayListOf<String>()
                listCountry.add("Выберите страну")
                for (i in 0 until countryRES.body()?.data!!.size) {
                    listCountry.add(countryRES.body()?.data!![i].name)
                }

                val items = List(listCountry.size) { i ->
                    SearchableItem(i.toLong(), listCountry[i])
                }

                view.createSpinCountry.items = items

                view.createSpinCountry.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            vv: View?,
                            position: Int,
                            id: Long,
                        ) {
                            justTime = justTime!! + 1
                            for (i in 0 until countryRES.body()?.data!!.size) {
                                if (countryRES.body()?.data!![i].name == items[position].title) {
                                    countryIndex = countryRES.body()?.data!![i].id
                                }
                            }
                            if (position == 0) {
                                view.textInputLayoutPro7.visibility = View.INVISIBLE
                                countryIndex = 0
                            } else {
                                if (justTime!! >= 2) {
                                    countryIndex?.let { selectCityFun(it) }
                                }
                            }

                        }
                    }

            }
        }


        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }
        return view.root
    }

    private fun uploadProduct() {

        params["experience"] = rb(binding.createEdtExperience.text.toString().trim())
        params["departure"] = rb(binding.createSpinDeparture.selectedItemPosition.toString())
        params["city_id"] = rb("$cityIndex")


        mHomeViewModel.pushSpecialistCreate(
            "Bearer $TOKEN_USER",
            params,
            params2,
            params3,
            params4,
            filePart
        )
        mHomeViewModel.mySpecialistCreate.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                getSpecialistIDSTATE = true
                MY_SPECIALIST = list.body()?.data?.id!!
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    withStyle()
                }
            } else {
                binding.createLoader.visibility = View.GONE
                binding.textTitle.text = resources.getString(R.string.create_specialist)
                binding.clickUpdateBackCard.visibility = View.VISIBLE
                binding.fonCreate.visibility = View.VISIBLE
                val sem = list.errorBody()?.source()?.buffer?.snapshot()?.utf8().toString()
                alertDialogCancel(sem)
            }

        }


    }

    private fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
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

    @SuppressLint("Recycle")
    private fun showSingleImage(uri: Uri) {
        try {
            binding.ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(requireContext()).load(uri)
                .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
                .into(binding.ivImage)
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

    private fun selectCityFun(int: Int) {
        binding.textInputLayoutPro7.visibility = View.VISIBLE
        mHomeViewModel.getCity(int)
        mHomeViewModel.myCity.observe(viewLifecycleOwner) { cityRES ->
            if (cityRES.isSuccessful) {
                val listCity = arrayListOf<String>()
                for (i in 0 until cityRES.body()?.data!!.children.size) {
                    listCity.add(cityRES.body()?.data!!.children[i].name)
                }

                val items = List(listCity.size) { i ->
                    SearchableItem(i.toLong(), listCity[i])
                }

                binding.createSpinCity.items = items

                binding.createSpinCity.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            for (i in 0 until cityRES.body()?.data!!.children.size) {
                                if (cityRES.body()?.data!!.children[i].name == items[position].title) {
                                    cityIndex = cityRES.body()?.data!!.children[i].id
                                }
                            }
                        }
                    }
            }
        }

    }

    @Suppress("DEPRECATION")
    private fun withStyle() {

        SmartDialogFragment.Builder((activity as AppCompatActivity).supportFragmentManager)
            .setIcon(SmartIcon.SUCCESS)
            .setTitle(resources.getText(R.string.successfully))
            .setMessage(resources.getText(R.string.yeah_specialist))
            .setPositiveButton("Выйти") {
                getCategoryID = -1
                getBrandID = -1
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
        if (requestCode == 82 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("resultSpecialistSkill")
            if (result != null) {
                binding.createEdtSkills.text = result
            }
        } else if (requestCode == 83 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("resultSelectSpecialist")
            if (result != null) {
                binding.createEdtCategory.text = result
            }
        }else if (requestCode == 84 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("resultSpecialistSelect")
            if (result != null) {
                binding.createEdtSpecialization.text = result
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}