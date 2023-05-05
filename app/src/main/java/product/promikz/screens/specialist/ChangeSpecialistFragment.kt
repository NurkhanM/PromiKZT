package product.promikz.screens.specialist

import android.annotation.SuppressLint
import android.app.Dialog
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
import product.promikz.R
import product.promikz.screens.create.newSpecialist.selectedCategory.CategorySelectSpecialistFragment
import product.promikz.screens.create.newSpecialist.selectedCategory.SkillsSelectSpecialistFragment
import product.promikz.viewModels.HomeViewModel
import com.google.android.gms.common.api.ApiException
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
import product.promikz.AppConstants.COUNTRY_ID
import product.promikz.AppConstants.MY_SPECIALIST
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.params
import product.promikz.AppConstants.params2
import product.promikz.AppConstants.params3
import product.promikz.AppConstants.params4
import product.promikz.databinding.FragmentChangeSpecialistBinding
import product.promikz.screens.create.newProduct.country.CounterSelectFragment
import product.promikz.screens.create.newSpecialist.selectedCategory.SpecializationSelectSpecialistFragment
import java.io.File

class ChangeSpecialistFragment : Fragment() {

    private var _binding: FragmentChangeSpecialistBinding? = null
    private val binding get() = _binding!!


    private lateinit var mHomeViewModel: HomeViewModel
    lateinit var dialog: Dialog
    private var stateSelectImageFirst = true
    private var filePart: MultipartBody.Part? = null
    private var uri: Uri? = null

    @SuppressLint("CommitTransaction")
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentChangeSpecialistBinding.inflate(inflater, container, false)
        val view = binding
        dialog = Dialog(requireContext())

        val departure = resources.getStringArray(R.array.Departure)
        val arrayAdapterState = ArrayAdapter(requireContext(), R.layout.item_dropdown, departure)
        view.createSpinDeparture.adapter = arrayAdapterState


        view.textInputLayoutPro3.setOnClickListener {
            product.promikz.AppConstants.specialistAllNumber.clear()
            view.createEdtSkills.text = ""
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .add(SkillsSelectSpecialistFragment(), "get")
                .commit()
        }


        view.textInputLayoutPro4.setOnClickListener {
            product.promikz.AppConstants.specialistAllNumber2.clear()
            view.createEdtCategory.text = ""
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .add(CategorySelectSpecialistFragment(), "get")
                .commit()
        }

        view.textInputLayoutSpecialization.setOnClickListener {
//            specialistAllNumber2.clear()
            view.createEdtSpecialization.text = ""
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .add(SpecializationSelectSpecialistFragment(), "get")
                .commit()
        }

        view.postProductBTN.setOnClickListener {

            view?.createLoader?.visibility = View.VISIBLE
            view?.textTitle?.text = resources.getString(R.string.wait)
            view?.clickUpdateBackCard?.visibility = View.GONE
            view?.fonCreate?.visibility = View.GONE

            if (view.createEdtExperience.length() != 0 && COUNTRY_ID != 0
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
                view?.createLoader?.visibility = View.GONE
                view?.textTitle?.text = resources.getString(R.string.change_specialist)
                view?.clickUpdateBackCard?.visibility = View.VISIBLE
                view?.fonCreate?.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.add_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        upload()

        setNormalSingleButton()


        view.edtNameCity.setOnClickListener{
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .add(CounterSelectFragment(), "CreateProduct")
                .commit()
        }






        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return view.root
    }

    private fun upload() {
        try {
            mHomeViewModel.getSpecialistShow(
                "Bearer $TOKEN_USER",
                MY_SPECIALIST
            )
            mHomeViewModel.mySpecialistShow.observe(viewLifecycleOwner) { list ->

                if (list.isSuccessful) {
                    params2.clear()
                    params3.clear()
                    params4.clear()

                    Glide.with(requireContext())
                        .load(list.body()?.data?.img)
                        .thumbnail(Glide.with(requireActivity()).load(R.drawable.loader2))
                        .centerCrop()
                        .into(binding.ivImage)


                    binding.createEdtExperience.setText(list.body()?.data?.experience.toString())

                    if (list.body()?.data?.skills?.isNotEmpty() == true) {
                        var str = ""
                        list.body()?.data?.skills?.forEach {
                            str += "\n" + it.name + "\n"
                        }
                        binding.createEdtSkills.text = str
                    }

                    if (list.body()?.data?.specializations?.isNotEmpty() == true) {
                        var str = ""
                        list.body()?.data?.specializations?.forEach {
                            str += "\n" + it.name + "\n"
                        }
                        binding.createEdtSpecialization.text = str
                    }

                    COUNTRY_ID = list.body()?.data?.city?.id
                    binding.edtNameCity.setText(list.body()?.data?.city?.name)

                    if (list.body()?.data?.categories?.isNotEmpty() == true) {
                        var str = ""
                        list.body()?.data?.categories?.forEach {
                            str += "\n" + it.name + "\n"
                        }
                        binding.createEdtCategory.text = str
                    }

                    for (i in 0 until list.body()?.data?.skills?.size!!) {
                        params3["skills[${list.body()?.data?.skills!![i].id}]"] =
                            rb("${list.body()?.data?.skills!![i].id}")
                    }

                    for (i in 0 until list.body()?.data?.categories?.size!!) {
                        params2["categories[${list.body()?.data?.categories!![i].id}]"] =
                            rb("${list.body()?.data?.categories!![i].id}")
                    }

                    for (i in 0 until list.body()?.data?.specializations?.size!!) {
                        params4["specializations[${list.body()?.data?.specializations!![i].id}]"] =
                            rb("${list.body()?.data?.specializations!![i].id}")
                    }

                }

            }


        } catch (e: ApiException) {
            e.printStackTrace()
        }


        mHomeViewModel.mySpecialistPut.observe(viewLifecycleOwner) { list ->


            if (list.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    withStyle()
                }
            } else {

                binding?.createLoader?.visibility = View.GONE
                binding?.textTitle?.text = resources.getString(R.string.change_specialist)
                binding?.clickUpdateBackCard?.visibility = View.VISIBLE
                binding?.fonCreate?.visibility = View.VISIBLE

                val sem = list.errorBody()?.source()?.buffer?.snapshot()?.utf8().toString()
                alertDialogCancel(sem)
            }

        }

    }

    private fun uploadProduct() {
        params["_method"] = rb("put")
        params["experience"] = rb(binding.createEdtExperience.text.toString().trim())
        params["departure"] = rb(binding.createSpinDeparture.selectedItemPosition.toString())
        params["city_id"] = rb(COUNTRY_ID.toString())


        mHomeViewModel.pushSpecialistPut(
            "Bearer $TOKEN_USER",
            MY_SPECIALIST,
            params,
            params2,
            params3,
            params4,
            filePart
        )


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

    @Suppress("DEPRECATION")
    private fun withStyle() {

        SmartDialogFragment.Builder((activity as AppCompatActivity).supportFragmentManager)
            .setIcon(SmartIcon.SUCCESS)
            .setTitle(resources.getText(R.string.successfully))
            .setMessage(resources.getText(R.string.yeah_specialist))
            .setPositiveButton(resources.getText(R.string.exit)) {
                product.promikz.AppConstants.getCategoryID = -1
                product.promikz.AppConstants.getBrandID = -1
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}