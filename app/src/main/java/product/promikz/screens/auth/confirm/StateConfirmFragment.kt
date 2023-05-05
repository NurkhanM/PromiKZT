package product.promikz.screens.auth.confirm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import product.promikz.AppConstants
import product.promikz.R
import product.promikz.MyUtils.uGlide
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentStateConfirmBinding
import product.promikz.viewModels.HomeViewModel

class StateConfirmFragment : Fragment() {


    private var stPhone = false
    private var stEmail = false

    lateinit var viewModel: HomeViewModel

    private var _binding: FragmentStateConfirmBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentStateConfirmBinding.inflate(inflater, container, false)




        binding.sBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        viewModel.myUser.observe(viewLifecycleOwner) { res ->

            if (res.isSuccessful) {

                binding.userPhone.text = res.body()?.data?.phone
                binding.userEmail.text = res.body()?.data?.email

                if (res.body()?.data?.phone_verified_at != null) {
                    binding.isStatePhoneTrue.visibility = View.VISIBLE
                    binding.isStatePhoneFalse.visibility = View.GONE
                    uGlide(requireContext(),binding.imgPhone, resources.getDrawable(R.drawable.ic_verified))
                    stPhone = true
                } else {
                    binding.isStatePhoneTrue.visibility = View.GONE
                    binding.isStatePhoneFalse.visibility = View.VISIBLE
                    clickPhone()
                    uGlide(requireContext(),binding.imgPhone, resources.getDrawable(R.drawable.ic_delete))
                    stPhone = false
                }

                if (res.body()?.data?.email_verified_at != null) {
                    binding.isStateEmailTrue.visibility = View.VISIBLE
                    binding.isStateEmailFalse.visibility = View.GONE
                    uGlide(requireContext(),binding.imgEmail, resources.getDrawable(R.drawable.ic_verified))
                    stEmail = true
                } else {
                    binding.isStateEmailTrue.visibility = View.GONE
                    binding.isStateEmailFalse.visibility = View.VISIBLE
                    clickEmail()
                    uGlide(requireContext(),binding.imgEmail, resources.getDrawable(R.drawable.ic_delete))
                    stEmail = false
                }


            } else {
                uToast(requireContext(), "Error confirm")
            }

        }


        return binding.root
    }

    private fun clickPhone(){
        binding.nextPhoneActive.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_stateConfirmFragment_to_phoneFragment)
        }
    }

    private fun clickEmail(){
        binding.nextEmailActive.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_stateConfirmFragment_to_emailFragment)
        }
    }


    override fun onResume() {
        super.onResume()

        viewModel.getUser("Bearer ${AppConstants.TOKEN_USER}")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}