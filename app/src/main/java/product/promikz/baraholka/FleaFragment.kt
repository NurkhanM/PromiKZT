package product.promikz.baraholka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentFleaBinding

@Suppress("DEPRECATION")
class FleaFragment : Fragment() {
    private var _binding: FragmentFleaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFleaBinding.inflate(inflater, container, false)

        binding.fleaBack.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}