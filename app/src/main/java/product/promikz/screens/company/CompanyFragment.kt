package product.promikz.screens.company

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.promikz.AppConstants
import product.promikz.MyUtils.uToast
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

        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.phoneSupport.setOnClickListener {
            callPhone()
        }

        binding.btnSend.setOnClickListener {
            binding.editSupport.setText("")
            binding.editSupport.visibility = View.GONE
            binding.btnSend.visibility = View.GONE
            binding.textDescription.text =
                "Мы успешно приняли ваше сообщение. Мы ответим вам в кратчайшие сроки"
            uToast(requireContext(), "Отправлено")
        }


        return binding.root
    }


    private fun callPhone() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${binding.phoneSupport.text}")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}