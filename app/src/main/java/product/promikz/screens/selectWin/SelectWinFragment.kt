package product.promikz.screens.selectWin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import product.promikz.R
import product.promikz.baraholka.FleaActivity
import product.promikz.databinding.FragmentSelectWinBinding

class SelectWinFragment : Fragment() {


    private var _binding: FragmentSelectWinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectWinBinding.inflate(inflater, container, false)
        val view = binding

        view.nextMachine.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_selectWinFragment_to_nav_view_menu_home)
        }

        view.nextFlea.setOnClickListener {
            val intent = Intent(requireContext(), FleaActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity)
                .overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    activity!!.finish()

                }
            })

        return view.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}