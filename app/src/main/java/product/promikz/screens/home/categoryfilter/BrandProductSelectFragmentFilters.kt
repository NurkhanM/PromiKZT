package product.promikz.screens.home.categoryfilter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.getBrandID
import product.promikz.AppConstants.getCategoryFiltersSortID
import product.promikz.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import product.promikz.databinding.FragmentBrandProductSelectBinding
import product.promikz.inteface.IClickListnearShops
import product.promikz.screens.create.newProduct.brand.BrandSelectAdapter

class BrandProductSelectFragmentFilters : BottomSheetDialogFragment() {

    private var _binding: FragmentBrandProductSelectBinding? = null
    private val binding get() = _binding!!

    val intent = Intent()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: BrandSelectAdapter


    private lateinit var txt: TextView

    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentBrandProductSelectBinding.inflate(inflater, container, false)
        val view = binding


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


        val adapter = BrandSelectAdapter(object : IClickListnearShops {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                getBrandID = int
                intent.putExtra("resultBrandProduct", name)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }


        })


        recyclerView = view.rvBrandSelect
        recyclerView.adapter = adapter


        mHomeViewModel.getCategoryID("Bearer $TOKEN_USER", getCategoryFiltersSortID)
        mHomeViewModel.myGetCategoryID.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                view.progressNewCreateBrand.visibility = View.GONE
                if (user.body()?.data?.brands?.isNotEmpty() == true) {
                    user.body()?.data.let { it?.let { it1 -> adapter.setData(it1.brands) } }
                } else {
                    view.txtErrorBrand.visibility = View.VISIBLE
                }

            }

        }
        binding.cancel.setOnClickListener { dismiss() }

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