package product.promikz.screens.hometwo.gaid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentGaidAdsBinding
import product.promikz.databinding.FragmentGaidAuthBinding

class GaidAdsFragment : Fragment() {

    private var _binding: FragmentGaidAdsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGaidAdsBinding.inflate(inflater, container, false)


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