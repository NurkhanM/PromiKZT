package product.promikz.screens.create.newSpecialist.selectedCategory

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.params2
import product.promikz.AppConstants.specialistAllNumber2
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import product.promikz.AppConstants
import product.promikz.AppConstants.MAP_SEARCH_DOP_FILTERS
import product.promikz.databinding.FragmentCategorySelectSpecialistBinding

class CategorySelectSpecialistFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCategorySelectSpecialistBinding? = null
    private val binding get() = _binding!!

    val intent = Intent()

    private var categoryIndexName = ArrayList<String>()
    private var categoryName = ArrayList<String>()
    private var categoryNameText = ArrayList<String>()
    private var categoryIndexId = ArrayList<Int>()
    private val checkBoxes = ArrayList<CheckBox>()

    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCategorySelectSpecialistBinding.inflate(inflater, container, false)
        val view = binding

        categoryIndexName.clear()
        categoryName.clear()
        categoryIndexId.clear()
        categoryNameText.clear()
        checkBoxes.clear()
        MAP_SEARCH_DOP_FILTERS.clear()
        params2.clear()
        specialistAllNumber2.clear()


        // Определите радиус скругления краев
        val radius = resources.getDimension(R.dimen.corner_radius)

        // Создайте объект ShapeAppearanceModel с радиусом скругления
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        // Получите ссылку на корневую View и установите для нее фон с использованием ShapeAppearanceModel
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.background = MaterialShapeDrawable(shapeAppearanceModel)
            .apply {
                setTint(ContextCompat.getColor(requireContext(), R.color.white))
            }


        mHomeViewModel.getCategoryIndex("Bearer $TOKEN_USER")
        mHomeViewModel.myCategoryIndex.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                view.progressNewCreatePro.visibility = View.GONE

                for (i in 0 until user.body()?.data?.size!!) {
                    if (user.body()?.data!![i].children!!.isEmpty()) {
                        categoryName.add(user.body()?.data!![i].name!!)
                        categoryIndexId.add(user.body()?.data!![i].id)
                    } else {
                        for (j in 0 until user.body()?.data!![i].children!!.size) {
                            if (user.body()?.data!![i].children!![j].children.isEmpty()) {
                                categoryName.add(user.body()?.data!![i].children!![j].name)
                                categoryIndexId.add(user.body()?.data!![i].children!![j].id)
                            } else {
                                for (k in 0 until user.body()?.data!![i].children!![j].children.size) {
                                    if (user.body()?.data!![i].children!![j].children[k].children.isEmpty()) {
                                        categoryName.add(user.body()?.data!![i].children!![j].children[k].name)
                                        categoryIndexId.add(user.body()?.data!![i].children!![j].children[k].id)
                                    }
                                }
                            }
                        }
                    }
                }

            }

            uploadCategoryAll()

        }
        params2.clear()
        view.textHeaderCategory.setOnClickListener {
            params2.clear()
            dismiss()

        }
        view.addCategory.setOnClickListener {

            intent.putExtra("resultSelectSpecialist", categoryNameText.joinToString(
                prefix = "",
                postfix = "",
                separator = ", \n\n"
            ))
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            // <1•2•3•4•5•6>

            dismiss()
        }



        return view.root
    }


    private fun isCHBListener(checkBox: CheckBox, id: Int) {


        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (isContains(specialistAllNumber2, id)) {
                    specialistAllNumber2 = removeItem(specialistAllNumber2, id)
                    MAP_SEARCH_DOP_FILTERS.keys.removeAll { it == "categories[${categoryIndexId[id]}]" }
                    params2.keys.removeAll { it == "categories[${categoryIndexId[id]}]" }
                    categoryNameText.remove(categoryIndexName[id])
                } else {
                    if (specialistAllNumber2.size > 3) {
                        checkBox.isChecked = false
                        Toast.makeText(
                            requireContext(),
                            resources.getText(R.string.maximum_4),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        specialistAllNumber2.add(id)
                        categoryNameText.add(categoryIndexName[id])
                        MAP_SEARCH_DOP_FILTERS["categories[${categoryIndexId[id]}]"] = categoryIndexId[id].toString()
                        params2["categories[${categoryIndexId[id]}]"] = rb(categoryIndexId[id].toString())
                    }
                }
            } else {
                specialistAllNumber2 = removeItem(specialistAllNumber2, id)
                MAP_SEARCH_DOP_FILTERS.keys.removeAll { it == "categories[${categoryIndexId[id]}]" }
                params2.keys.removeAll { it == "categories[${categoryIndexId[id]}]" }
                categoryNameText.remove(categoryIndexName[id])
            }

        }


    }

    private fun isContains(array: java.util.ArrayList<Int>, value: Int): Boolean {
        return array.contains(value)
    }

    private fun removeItem(array: java.util.ArrayList<Int>, value: Int): java.util.ArrayList<Int> {
        val arr = array.filter { it != value }.toIntArray()
        return arr.toCollection(java.util.ArrayList())
    }


    private fun rb(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }



    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun uploadCategoryAll() {


        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.topMargin = 20


        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val layoutCheckBox = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        )
        layoutView.topMargin = 40


        binding.inElectron.removeAllViews()
        checkBoxes.clear()

        for (i in 0 until categoryName.size) {

            val linearLayout2 = LinearLayout(binding.inElectron.context)
            linearLayout2.removeAllViews()
            linearLayout2.orientation = LinearLayout.HORIZONTAL
            linearLayout2.layoutParams = layoutParams


            val viewLine = View(binding.inElectron.context)
            viewLine.layoutParams = layoutView
            viewLine.marginTop.div(15)
            viewLine.background = requireContext().resources.getDrawable(R.color.fon1)

            val textView = TextView(linearLayout2.context)
            textView.textSize = 16f
            textView.text = categoryName[i]
            textView.layoutParams = layoutText


            val checBox = CheckBox(linearLayout2.context)
            checBox.layoutParams = layoutCheckBox


            linearLayout2.addView(textView)
            linearLayout2.addView(checBox)

            binding.inElectron.addView(linearLayout2)
            binding.inElectron.addView(viewLine)

            categoryIndexName.add(textView.text.toString())
            checkBoxes.add(checBox)

        }

        checkBoxes.forEachIndexed { index, item ->
            isCHBListener(item, index)
        }


    }


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null)
        dialog.setContentView(view)
        dialog.behavior.isHideable = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}