package product.promikz.screens.create

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import product.promikz.*
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_TYPE
import product.promikz.screens.create.newProduct.ProductCreateActivity
import product.promikz.screens.create.newSpecialist.SpecialistCreateActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import product.promikz.AppConstants.VERIFY_USER_PHONE
import product.promikz.AppConstants.getSpecialistIDSTATE
import product.promikz.MyUtils.uGlide
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentFirstCreateBinding
import product.promikz.screens.auth.AuthActivity
import product.promikz.screens.create.newStudyCourses.StudyCoursesCreateActivity
import product.promikz.screens.create.newTradeIn.TradeInCreateActivity

class FirstCreateFragment : Fragment() {

    private var _binding: FragmentFirstCreateBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstCreateBinding.inflate(inflater, container, false)
        val view = binding

        view.buttonCreateProducts.setOnClickListener {
            if (stateRequest()) {
                val intent = Intent(requireActivity(), ProductCreateActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }
        }

        view.buttonCreateSpecialist.setOnClickListener {
            if (stateRequest()) {
                val intent = Intent(requireActivity(), SpecialistCreateActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }
        }

        view.buttonCreateProductsForTradeIn.setOnClickListener {
            if (stateRequest()) {
                val intent = Intent(requireActivity(), TradeInCreateActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }
        }

        view.buttonCreateProductsForUcheb.setOnClickListener {
            if (stateRequest()) {
                val intent = Intent(requireActivity(), StudyCoursesCreateActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }
        }



        return view.root
    }


    override fun onResume() {
        super.onResume()

        if (USER_TYPE == "0") {
            binding?.buttonCreateProductsForTradeIn?.visibility = View.GONE
            binding?.buttonCreateProductsForUcheb?.visibility = View.GONE
        }

        if (USER_TYPE == "1") {
            binding?.buttonCreateProductsForUcheb?.visibility = View.GONE
        }

        if (USER_TYPE == "2") {
            binding?.buttonCreateProductsForTradeIn?.visibility = View.GONE
        }

        if (getSpecialistIDSTATE) {
            binding?.buttonCreateSpecialist?.visibility = View.GONE
        } else {
            binding?.buttonCreateSpecialist?.visibility = View.VISIBLE
        }

    }


    private fun stateRequest(): Boolean {

        binding.buttonCreateSpecialist.isClickable = false
        binding.buttonCreateProducts.isClickable = false
        binding.buttonCreateProductsForTradeIn.isClickable = false
        binding.buttonCreateProductsForUcheb.isClickable = false

        var boolean = false

        if (TOKEN_USER != "null") {
            if (VERIFY_USER_PHONE) {
                if (ID_SHOP_USER != -1) {
                    boolean = true
                    binding.buttonCreateSpecialist.isClickable = true
                    binding.buttonCreateProducts.isClickable = true
                    binding.buttonCreateProductsForTradeIn.isClickable = true
                    binding.buttonCreateProductsForUcheb.isClickable = true
                } else {

                    uGlide(requireContext(), binding.imageView, R.drawable.create_shop_img)

                    binding.textView.text = resources.getString(R.string.create_shop_text)
                    binding.buttonCreateProductsForTradeIn.isClickable = false
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        binding.buttonCreateProductsForTradeIn.isClickable = true
                        Glide.with(requireContext()).load(R.drawable.sell_machine)
                            .thumbnail(
                                Glide.with(requireContext())
                                    .load(R.drawable.loader2)
                            )
                            .centerCrop().into(binding.imageView)
                        binding.textView.text = resources.getString(R.string.easy_buy)
                        binding.buttonCreateSpecialist.isClickable = true
                        binding.buttonCreateProducts.isClickable = true
                        binding.buttonCreateProductsForTradeIn.isClickable = true
                        binding.buttonCreateProductsForUcheb.isClickable = true
                    }
                }
            } else {
                uGlide(requireContext(), binding.imageView, R.drawable.gmail)
                binding.textView.text = resources.getString(R.string.gmail_create)
                binding.buttonCreateProductsForTradeIn.isClickable = false
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    binding.buttonCreateProductsForTradeIn.isClickable = true
                    uGlide(requireContext(), binding.imageView, R.drawable.sell_machine)
                    binding.textView.text = resources.getString(R.string.easy_buy)
                    binding.buttonCreateSpecialist.isClickable = true
                    binding.buttonCreateProducts.isClickable = true
                    binding.buttonCreateProductsForTradeIn.isClickable = true
                    binding.buttonCreateProductsForUcheb.isClickable = true
                }
            }
        } else {
            uGlide(requireContext(), binding.imageView,R.drawable.authtet)

            binding.textView.text = resources.getString(R.string.auth_create)
            binding.buttonCreateProductsForTradeIn.isClickable = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                binding.buttonCreateProductsForTradeIn.isClickable = true
                uGlide(requireContext(), binding.imageView, R.drawable.sell_machine)
                binding.textView.text = resources.getString(R.string.easy_buy)
                binding.buttonCreateSpecialist.isClickable = true
                binding.buttonCreateProducts.isClickable = true
                binding.buttonCreateProductsForTradeIn.isClickable = true
                binding.buttonCreateProductsForUcheb.isClickable = true
            }
        }

        return boolean
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        binding.crDrawer.setOnClickListener {
            activityBinding?.layoutDrawer?.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }

}