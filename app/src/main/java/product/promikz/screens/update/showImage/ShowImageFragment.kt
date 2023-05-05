package product.promikz.screens.update.showImage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.viewModels.HomeViewModel
import product.promikz.databinding.FragmentShowImageBinding
import product.promikz.models.products.show.Image

class ShowImageFragment : Fragment() {

    private var _binding: FragmentShowImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mImageArrayUpdate: HomeViewModel
    private var idProducts: Int = -2
    private var indexShow: Int = -2


    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mImageArrayUpdate = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentShowImageBinding.inflate(inflater, container, false)
        val view = binding

        val arguments = (activity as AppCompatActivity).intent.extras
        idProducts = arguments!!["show"] as Int
        indexShow = arguments["index"] as Int


        mImageArrayUpdate.getUpdateDataArrayUpdate("Bearer $TOKEN_USER", idProducts)
        mImageArrayUpdate.myShowProducts.observe(viewLifecycleOwner) { list ->

            val test = ArrayList<Image>()
            test.add(Image(0, "null" , list.body()?.data?.img.toString(),"null"))
            list.body()?.data?.images?.let { test.addAll(it) }
            view.viewPagerShow.adapter = ShowImageAdapterAdvert(test)
            view.viewPagerShow.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            view.viewPagerShow.currentItem = indexShow
            view.indicatorCircularShow.setViewPager((view.viewPagerShow))
        }

        view.clickShowBackCard.setOnClickListener{
            (activity as AppCompatActivity).onBackPressed()
        }

        return view.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}