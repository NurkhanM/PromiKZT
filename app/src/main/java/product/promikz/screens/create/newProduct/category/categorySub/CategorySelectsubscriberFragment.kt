package product.promikz.screens.create.newProduct.category.categorySub

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
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.getCategoryID
import product.promikz.inteface.IClickListnearShops
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import product.promikz.AppConstants
import product.promikz.databinding.CastomSelectSubscriberCategoryBinding
import product.promikz.databinding.FragmentCategorySelectBinding
import product.promikz.inteface.IClickListnearSubscriberCategory
import product.promikz.screens.create.newProduct.category.CategorySelectAdapter
import product.promikz.screens.create.newProduct.category.CategorySelectAdapterRekurcia

class CategorySelectsubscriberFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentCategorySelectBinding? = null
    private val binding get() = _binding!!


    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CategorySelectSubscriberAdapter

    val intent = Intent()

    var prevJust = ArrayList<Int>()
    private var justTime: Int? = 0

    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCategorySelectBinding.inflate(inflater, container, false)
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



        val adapter = CategorySelectSubscriberAdapter(object : IClickListnearSubscriberCategory {
            @SuppressLint("InflateParams", "SyntheticAccessor")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    prevJust.add(int)
                    justTime = justTime!! + 1
                    rekursia(int)
                } else {
                    AppConstants.getCategorySearchSortID = int
                    AppConstants.CATEGORY_INT_SEARCH_DATA = int
                    intent.putExtra("result", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    AppConstants.compareAll.clear()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }

            override fun clickListenerBoolean(
                boolean: Boolean,
                viewAdapter: CastomSelectSubscriberCategoryBinding, number: Int
            ) {
                if (!boolean) {
                    viewAdapter.imgSubscriber.setImageResource(R.drawable.ic_star2)
                } else {
                    viewAdapter.imgSubscriber.setImageResource(R.drawable.ic_star)
                }
                mHomeViewModel.subCategory("Bearer $TOKEN_USER", number)
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
        view.textHeader.setOnClickListener {

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

    @Suppress("DEPRECATION")
    private fun rekursia(int: Int) {

        val adapter = CategorySelectSubAdapterRekurcia(object : IClickListnearSubscriberCategory {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                if (boolean) {
                    prevJust.add(int)
                    justTime = justTime!! + 1
                    rekursia(int)
                } else {
                    AppConstants.getCategorySearchSortID = int
                    AppConstants.CATEGORY_INT_SEARCH_DATA = int
                    intent.putExtra("result", name)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    AppConstants.compareAll.clear()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }

            override fun clickListenerBoolean(
                boolean: Boolean,
                viewAdapter: CastomSelectSubscriberCategoryBinding, number: Int
            ) {
                if (!boolean) {
                    viewAdapter.imgSubscriber.setImageResource(R.drawable.ic_star2)
                } else {
                    viewAdapter.imgSubscriber.setImageResource(R.drawable.ic_star)
                }
                mHomeViewModel.subCategory("Bearer $TOKEN_USER", number)
            }

        })
        val recyclerView: RecyclerView = binding.rvCategorySelect
        recyclerView.adapter = adapter
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