package product.promikz.screens.create.newProduct.category

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
import product.promikz.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.compareAll
import product.promikz.AppConstants.getCategorySearchSortID
import product.promikz.inteface.IClickListnearShops
import product.promikz.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import product.promikz.AppConstants.CATEGORY_INT_SEARCH_DATA
import product.promikz.databinding.FragmentCategorySelectBinding

class CategorySelectFragmentSearch : BottomSheetDialogFragment() {

    private var _binding: FragmentCategorySelectBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CategorySelectAdapter


    var prevJust = ArrayList<Int>()
    private var justTime: Int? = 0

    val intent = Intent()

    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategorySelectBinding.inflate(inflater, container, false)
        val view = binding
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


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


        val adapter = CategorySelectAdapter(object : IClickListnearShops {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    prevJust.add(int)
                    justTime = justTime!! + 1
                    rekursia(int)
                } else {
                    getCategorySearchSortID = int
                    CATEGORY_INT_SEARCH_DATA = int
                    intent.putExtra("result", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                    compareAll.clear()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                }

            }
        })

        recyclerView = view.rvCategorySelect
        recyclerView.adapter = adapter




        mHomeViewModel.getCategoryIndex("Bearer $TOKEN_USER")
        mHomeViewModel.myCategoryIndex.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                view.progressNewCreatePro.visibility = View.GONE
                user.body()?.let { adapter.setData(it.data) }
            }

        }
        binding.textHeader.setOnClickListener {

            if (justTime!! != 0 || justTime!! > 0) {
                justTime = justTime!! - 1
                if (justTime!! == 0) {
                    recyclerView = view.rvCategorySelect
                    recyclerView.adapter = adapter
                    mHomeViewModel.myCategoryIndex.observe(viewLifecycleOwner) { user ->
                        if (user.isSuccessful) {
                            view.progressNewCreatePro.visibility = View.GONE
                            user.body()?.let { adapter.setData(it.data) }
                        }

                    }
                } else {
                    rekursia(prevJust[justTime!!])
                }

            } else {
                dismiss()
            }


        }
        binding.cancel.setOnClickListener { dismiss() }

        return view.root
    }

    private fun rekursia(int: Int) {

        val adapter = CategorySelectAdapterRekurcia(object : IClickListnearShops {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    prevJust.add(int)
                    justTime = justTime!! + 1
                    rekursia(int)
                } else {
                    getCategorySearchSortID = int
                    CATEGORY_INT_SEARCH_DATA = int
                    intent.putExtra("result", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                    compareAll.clear()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                }
            }

        })
        val recyclerView: RecyclerView? = binding?.rvCategorySelect
        recyclerView?.adapter = adapter
        val mHomeViewModel: HomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        mHomeViewModel.getCategoryID("Bearer $TOKEN_USER", int)
        mHomeViewModel.myGetCategoryID.observe(viewLifecycleOwner) { res ->
            if (res.isSuccessful) {
                res.body()?.data.let {
                    it?.let { it1 ->
                        it1.children?.let { it2 ->
                            adapter.setData(
                                it2
                            )
                        }
                    }
                }
            }
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