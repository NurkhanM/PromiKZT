package product.promikz.screens.specialist.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentFilterSpecialistBinding

class FilterSpecialistFragment : Fragment() {

    private var _binding: FragmentFilterSpecialistBinding? = null
    private val binding get() = _binding!!

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFilterSpecialistBinding.inflate(inflater, container, false)


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