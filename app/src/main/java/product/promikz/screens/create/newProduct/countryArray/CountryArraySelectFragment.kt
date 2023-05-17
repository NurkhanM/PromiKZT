package product.promikz.screens.create.newProduct.countryArray

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import product.promikz.AppConstants.COUNTRY_ID_ARRAY
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentCategorySelectBinding
import product.promikz.inteface.IClickListnearFIlterCountry
import product.promikz.models.country.index.Data
import product.promikz.viewModels.HomeViewModel

class CountryArraySelectFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCategorySelectBinding? = null
    private val binding get() = _binding!!

    val intent = Intent()

    lateinit var recyclerView: RecyclerView
    private lateinit var adapterCounter: CountryArraySelectAdapter

    lateinit var viewModel: HomeViewModel

    private var arrayCountryAll = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCategorySelectBinding.inflate(inflater, container, false)
        val view = binding
        COUNTRY_ID_ARRAY.clear()


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



        recyclerView = view.rvCategorySelect
        adapterCounter = CountryArraySelectAdapter(object : IClickListnearFIlterCountry {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    rekursia(int)
                } else {
                    COUNTRY_ID_ARRAY.add(int)
                    intent.putExtra("result", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }

            override fun clickListenerFilter(int: Int, name: String, boolean: Boolean) {

                COUNTRY_ID_ARRAY.clear()

                arrayCountryAll.forEach { idList ->

                    if (int == idList.id){
                        if (idList.children?.isNotEmpty() == true) {
                            idList.children.forEach { idChild ->
                                COUNTRY_ID_ARRAY.add(idChild.id)
                            }
                        }
                    } else{
                        COUNTRY_ID_ARRAY.add(int)
                    }

                }

                intent.putExtra("result", name)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
        })


        recyclerView.adapter = adapterCounter


        viewModel.getCountry()
        viewModel.myCountry.observe(viewLifecycleOwner) { countryRES ->
            if (countryRES.isSuccessful) {
                countryRES.body()?.data?.let {
                    view?.progressNewCreatePro?.visibility = View.GONE
                    adapterCounter.setData(it)
                    arrayCountryAll = it as ArrayList<Data>
                }
            } else {
                uToast(requireContext(), "Country error")
            }
        }

        view.cancel.setOnClickListener { dismiss() }

        return view.root
    }


    private fun rekursia(int: Int) {

        val arrayCountryRekursia = ArrayList<Data>()

        arrayCountryAll.forEach { list ->

            if (list.id == int) {
                list.children?.forEach {
                    arrayCountryRekursia.add(
                        Data(
                            null,
                            it.created_at,
                            it.id,
                            it.name,
                            it.parent_id
                        )
                    )
                }
            }
        }
        adapterCounter.setData(arrayCountryRekursia)
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