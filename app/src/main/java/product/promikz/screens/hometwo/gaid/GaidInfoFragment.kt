package product.promikz.screens.hometwo.gaid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import product.promikz.R
import product.promikz.databinding.FragmentGaidInfoBinding

class GaidInfoFragment: Fragment() {

    private var idGiad: Int = -1

    private var _binding: FragmentGaidInfoBinding? = null
    private val binding get() = _binding!!

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGaidInfoBinding.inflate(inflater, container, false)
        val arguments = (activity as AppCompatActivity).intent.extras
        idGiad = arguments!!["gaid"] as Int

        when (idGiad){
            0 -> binding.textGaid.text = resources.getString(R.string.todo)
            1 -> binding.textGaid.text = resources.getString(R.string.todo)
            2 -> binding.textGaid.text = resources.getString(R.string.todo)
            else -> binding.textGaid.text = resources.getString(R.string.todo)
        }

        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }


}