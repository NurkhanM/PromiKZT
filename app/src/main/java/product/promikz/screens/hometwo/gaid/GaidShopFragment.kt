package product.promikz.screens.hometwo.gaid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentGaidShopBinding

class GaidShopFragment: Fragment() {


    private var _binding: FragmentGaidShopBinding? = null
    private val binding get() = _binding!!

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGaidShopBinding.inflate(inflater, container, false)


        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }




        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}