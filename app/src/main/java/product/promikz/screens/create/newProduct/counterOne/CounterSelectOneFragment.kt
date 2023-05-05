package product.promikz.screens.create.newProduct.counterOne

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
import product.promikz.databinding.FragmentCounterSelectOneBinding
import product.promikz.inteface.IClickListnearShops
import product.promikz.models.country.index.Data
import product.promikz.viewModels.HomeViewModel

class CounterSelectOneFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCounterSelectOneBinding? = null
    private val binding get() = _binding!!

    val intent = Intent()

    lateinit var recyclerView: RecyclerView
    private lateinit var adapterCounter: CountrySelectOneAdapter

    lateinit var viewModel: HomeViewModel

    private var arrayCountryAll = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCounterSelectOneBinding.inflate(inflater, container, false)
        val view = binding


        // Определите радиус скругления краев
        val radius = resources.getDimension(R.dimen.corner_radius)

        // Создайте объект ShapeAppearanceModel с радиусом скругления
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        // Получите ссылку на корневую View и установите для нее фон с использованием ShapeAppearanceModel
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.background = MaterialShapeDrawable(shapeAppearanceModel)
            .apply {
                setTint(ContextCompat.getColor(requireContext(), R.color.white))
            }

        recyclerView = view.rvCategorySelect
        adapterCounter = CountrySelectOneAdapter(object : IClickListnearShops {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                COUNTRY_ID = int
                intent.putExtra("counterselectOne", name)
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

        view.cancelCity.setOnClickListener { dismiss() }

        return view.root
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