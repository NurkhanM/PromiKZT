package product.promikz.screens.error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.FragmentOldVersionBinding
import kotlin.system.exitProcess

class OldVersionFragment : Fragment() {

    private var _binding: FragmentOldVersionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOldVersionBinding.inflate(inflater, container, false)
        val view = binding

        view.seExit.setOnClickListener {
            exitProcess(0)
        }


        return view.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}