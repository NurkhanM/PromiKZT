package product.promikz.screens.hometwo.gaid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.databinding.ActivityAuthBinding
import product.promikz.databinding.FragmentGaidAuthBinding
import product.promikz.screens.auth.AuthActivity

class GaidAuthFragment : Fragment() {

    private var _binding: FragmentGaidAuthBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityAuthBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGaidAuthBinding.inflate(inflater, container, false)


        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? AuthActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        activityBinding?.bottomNavigation?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activityBinding = null
    }

}