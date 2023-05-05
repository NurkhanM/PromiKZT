package product.promikz.screens.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentCompanyBinding

class CompanyFragment : Fragment() {

    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding!!


    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)
        val view = binding

        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}