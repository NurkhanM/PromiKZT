package product.promikz.screens.complaint.product

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.getProductID
import product.promikz.R
import product.promikz.databinding.FragmentComplaintProductBinding
import product.promikz.viewModels.HomeViewModel

class ComplaintProductFragment : Fragment() {

    private var _binding: FragmentComplaintProductBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModels: HomeViewModel

    lateinit var dialog: Dialog

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentComplaintProductBinding.inflate(inflater, container, false)
        val view = binding

        dialog = Dialog(requireContext())


        view.radio1.setOnClickListener {
            view.radio1.isChecked = true
            view.radio2.isChecked = false
            view.radio3.isChecked = false
            view.radio4.isChecked = false
            view.radio5.isChecked = false
            view.radio6.isChecked = false
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }

        view.radio2.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = true
            view.radio3.isChecked = false
            view.radio4.isChecked = false
            view.radio5.isChecked = false
            view.radio6.isChecked = false
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }

        view.radio3.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = false
            view.radio3.isChecked = true
            view.radio4.isChecked = false
            view.radio5.isChecked = false
            view.radio6.isChecked = false
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }

        view.radio4.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = false
            view.radio3.isChecked = false
            view.radio4.isChecked = true
            view.radio5.isChecked = false
            view.radio6.isChecked = false
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }
        view.radio5.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = false
            view.radio3.isChecked = false
            view.radio4.isChecked = false
            view.radio5.isChecked = true
            view.radio6.isChecked = false
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }
        view.radio6.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = false
            view.radio3.isChecked = false
            view.radio4.isChecked = false
            view.radio5.isChecked = false
            view.radio6.isChecked = true
            view.radio7.isChecked = false
            view.editComplaint.visibility = View.GONE
        }

        view.radio7.setOnClickListener {
            view.radio1.isChecked = false
            view.radio2.isChecked = false
            view.radio3.isChecked = false
            view.radio4.isChecked = false
            view.radio5.isChecked = false
            view.radio6.isChecked = false
            view.radio7.isChecked = true
            view.editComplaint.visibility = View.VISIBLE
        }


        view.btnSend.setOnClickListener {
            var nameRadio = "*"

            if (view.radio1.isChecked) {
                nameRadio = view.radioTxt1.text as String
            }

            if (view.radio2.isChecked) {
                nameRadio = view.radioTxt2.text as String
            }

            if (view.radio3.isChecked) {
                nameRadio = view.radioTxt3.text as String
            }

            if (view.radio4.isChecked) {
                nameRadio = view.radioTxt4.text as String
            }

            if (view.radio5.isChecked) {
                nameRadio = view.radioTxt5.text as String
            }

            if (view.radio6.isChecked) {
                nameRadio = view.radioTxt6.text as String
            }

            if (view.radio7.isChecked) {
                nameRadio = view.editComplaint.text.toString()
            }

            val viewRep: String = if (view.chkb1.isChecked){
                "0"
            } else {
                "1"
            }

            viewModels.postReport("Bearer $TOKEN_USER", getProductID,"product", "$nameRadio ${view.editComplaint.text.toString().trim()}", viewRep)
        }

        viewModels.myReport.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                Toast.makeText(requireContext(), "Ваша жалоба было успешно принято", Toast.LENGTH_SHORT).show()
//                alertDialogMessage("Успех", "Ваша жалоба было успешно принято")
                val intent = Intent()
                activity?.setResult(102, intent)
                activity?.finish()
            } else {
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(),
                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }


        }

        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return binding.root
    }

    private fun alertDialogMessage(title: String, descrip: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_error_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes_error)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_title_error)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_error)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}