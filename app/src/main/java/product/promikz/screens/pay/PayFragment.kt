package product.promikz.screens.pay

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.stream.MalformedJsonException
import org.json.JSONException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.ID_PAY
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentPayBinding
import product.promikz.viewModels.HomeViewModel
import java.lang.reflect.InvocationTargetException

class PayFragment : Fragment() {

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModels: HomeViewModel

    lateinit var dialog: Dialog
    private lateinit var dialogPay: Dialog
    private lateinit var dialogLoader: Dialog

    private val map: HashMap<String, String> = hashMapOf()


    @Suppress("NAME_SHADOWING", "DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentPayBinding.inflate(inflater, container, false)
        val view = binding

        dialog = Dialog(requireContext())
        dialogPay = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())

        view.nextVerify.setOnClickListener {
            ID_PAY = 4
            viewModels.getServices()
            alertDialogLoader()

        }

        viewModels.myServices.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {

                for (i in 0 until list?.body()?.data?.size!!) {

                    if (ID_PAY == list.body()?.data?.get(i)?.id) {
                        for (uID in 0 until list.body()?.data?.get(i)?.type!!.size) {
                            if (AppConstants.USER_TYPE == list.body()?.data?.get(i)?.type!![uID]) {
                                map.clear()
                                map["payId"] = ID_PAY.toString()
                                viewModels.payGenerate("Bearer $TOKEN_USER", map)
                                break
                            } else {
                                dialogLoader.dismiss()
                                alertDialogMessage(
                                    "Не доступно"
                                )
                            }
                        }
                        break
                    }
                }

            } else {

                try {
                    dialogLoader.dismiss()


                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))
                    var message = ""

                    for (name in jsonObjError.keys()){

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()){
                            message = nameArray.getString(i)
                        }

                    }


                    alertDialogMessage(message)
                }catch (i: MalformedJsonException){
                    uToast(requireContext(), "Error BackEnd(1.1)")
                }

            }


        }


        viewModels.myPayGenerate.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                dialogLoader.dismiss()
                    alertDialogPay(
                        list.body()?.data?.order?.description.toString(),
                        "Ваш тариф составляет " +
                                "${list.body()?.data?.order?.price} ${list.body()?.data?.order?.currency}",
                        list.body()?.data?.url.toString()
                    )

            } else {
                dialogLoader.dismiss()

                try {
                    val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                    val jsonObjError = JSONObject(jsonObj.getString("errors"))
                    var message = ""

                    for (name in jsonObjError.keys()){

                        val nameArray = jsonObjError.getJSONArray(name)

                        for (i in 0 until nameArray.length()){
                            message = nameArray.getString(i)
                        }

                    }

                    alertDialogMessage(message)

                }catch (e: RuntimeException){
                    uToast(requireContext(), "Error BackEnd(2)")
                }catch (j: JSONException){
                    uToast(requireContext(), "Error BackEnd(3)")
                }catch (i: InvocationTargetException){
                    uToast(requireContext(), "Error BackEnd(4)")
                }


            }
        }

        view.nextBanner.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_payFragment_to_buyBannerFragment)

        }

        view.nextStory.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_payFragment_to_buyStoryFragment)
        }

        view.nextAdsBetween.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_payFragment_to_buyAdsFragment)
        }


        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return view.root
    }


    private fun alertDialogMessage(descrip: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_error_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes_error)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_error)
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun alertDialogPay(title: String, descrip: String, url: String) {

        dialogPay.setCancelable(false)
        dialogPay.setCanceledOnTouchOutside(false)
        dialogPay.setContentView(R.layout.dialog_pay)
        dialogPay.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialogPay.findViewById<Button>(R.id.dialog_pay_yes)
        val buttonNOT = dialogPay.findViewById<Button>(R.id.dialog_pay_no)
        val textTitle = dialogPay.findViewById<TextView>(R.id.txt_title_pay)
        val textDescrip = dialogPay.findViewById<TextView>(R.id.txt_descript_pay)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                ContextCompat.startActivity(requireContext(), browserIntent, null)
            }catch (e: ActivityNotFoundException){
                uToast(requireContext(), "Error BackEnd")
            }
            dialogPay.dismiss()
        }
        buttonNOT.setOnClickListener {
            dialogPay.dismiss()
        }
        dialogPay.show()
    }

    private fun alertDialogLoader() {

        dialogLoader.setCancelable(false)
        dialogLoader.setCanceledOnTouchOutside(false)
        dialogLoader.setContentView(R.layout.dialog_loader)
        dialogLoader.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoader.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}