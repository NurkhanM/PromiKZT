package product.promikz.screens.create.newProduct.country

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
import product.promikz.AppConstants.COUNTRY_ID
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentCounterSelectBinding
import product.promikz.inteface.IClickListnearShops
import product.promikz.models.country.index.Data
import product.promikz.viewModels.HomeViewModel

class CounterSelectFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentCounterSelectBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    private lateinit var adapterCounter: CountrySelectAdapter

    lateinit var viewModel: HomeViewModel
    val intent = Intent()

    private var arrayCountryAll = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCounterSelectBinding.inflate(inflater, container, false)


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

        recyclerView = binding.rvCategorySelect
        adapterCounter = CountrySelectAdapter(object : IClickListnearShops {
            @SuppressLint("InflateParams", "SyntheticAccessor")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    rekursia(int)
                } else {
                    COUNTRY_ID = int
                    intent.putExtra("textCountryName", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }
        })


        recyclerView.adapter = adapterCounter




        viewModel.getCountry()
        viewModel.myCountry.observe(viewLifecycleOwner) { countryRES ->
            if (countryRES.isSuccessful) {
                countryRES.body()?.data?.let {
                    binding.progressNewCreatePro.visibility = View.GONE
                    adapterCounter.setData(it)
                    arrayCountryAll = it as ArrayList<Data>
                }
            } else {
                uToast(requireContext(), "Country error")
            }
        }

        binding.cancelCity.setOnClickListener { dismiss() }

        return binding.root
    }


    private fun rekursia(int: Int) {

        val arrayCountryRekursia = ArrayList<Data>()

        arrayCountryAll.forEach { list ->

            if (list.id == int){
                list.children?.forEach {
                    arrayCountryRekursia.add(Data(null, it.created_at, it.id, it.name, it.parent_id))
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