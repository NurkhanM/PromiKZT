package product.promikz.screens.shop

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import product.promikz.AppConstants.REVIEW_STATE
import product.promikz.AppConstants.getCategoryID
import product.promikz.AppConstants.getProductID
import product.promikz.inteface.IClickListnearShops
import product.promikz.viewModels.HomeViewModel
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MyUtils.uToast
import product.promikz.R
import product.promikz.databinding.FragmentReviewAllBinding

class ReviewAllFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentReviewAllBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ShopsAdapter


    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentReviewAllBinding.inflate(inflater, container, false)

        val adapter = ShopsAdapter(object : IClickListnearShops {
            @SuppressLint("InflateParams")
            override fun clickListener(int: Int, name: String, boolean: Boolean) {
                getCategoryID = int
                dismiss()
                Toast.makeText(requireContext(), "$int", Toast.LENGTH_SHORT).show()
            }

        })

        recyclerView = binding.rvShops
        recyclerView.adapter = adapter


        mHomeViewModel.myReviews.observe(viewLifecycleOwner) { user ->
            if (user.isSuccessful) {
                user.body()?.let { adapter.setData(it.data) }
                binding.progressNewCreatePro.visibility = View.GONE
                if (REVIEW_STATE){
                    binding.linShowReview.visibility = View.VISIBLE
                    binding.textState.visibility = View.GONE
                }else{
                    binding.linShowReview.visibility = View.GONE
                    binding.textState.visibility = View.VISIBLE
                }
            }
        }

        binding.cancel.setOnClickListener { dismiss() }

        binding.btnPostMessage.setOnClickListener {
            if (binding.edtNewMessage.text.isNotEmpty()) {
                mHomeViewModel.postReview("Bearer $TOKEN_USER",
                    binding.edtNewMessage.text.toString(),
                    "App\\Models\\Product",
                    getProductID
                )
                binding.edtNewMessage.setText("")
                REVIEW_STATE = false
            } else {
                uToast(requireContext(), "null field")
            }
        }

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

        mHomeViewModel.myReviewsPost.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                onResume()
                uToast(requireContext(), "Ваш отзыв успешно отправлен")
            }else{
                uToast(requireContext(), "Вы ранее уже отправляли отзыв")
            }
        }


        return binding.root

    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_review_all, null)
        dialog.setContentView(view)
        dialog.behavior.isHideable = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }

    override fun onResume() {
        super.onResume()

        mHomeViewModel.getReviews(getProductID)

    }
}