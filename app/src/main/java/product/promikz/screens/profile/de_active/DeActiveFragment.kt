package product.promikz.screens.profile.de_active

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearProfille
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import com.google.android.gms.common.api.ApiException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentDeActiveBinding
import product.promikz.databinding.ItemProfileModelsBinding
import product.promikz.screens.changeProduct.ChangeProductActivity
import java.util.HashMap

class DeActiveFragment : Fragment() {

    private var _binding: FragmentDeActiveBinding? = null
    private val binding get() = _binding!!
    private lateinit var mHomeViewModel: HomeViewModel
    lateinit var dialog: Dialog
    lateinit var dialogLoader: Dialog
    private lateinit var dialogPay: Dialog

    lateinit var recyclerView: RecyclerView
    private lateinit var activeAdapter: ProfileDeActiveAdapter

    val map: HashMap<String, String> = hashMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentDeActiveBinding.inflate(inflater, container, false)
        val view = binding
        dialog = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())
        dialogPay = Dialog(requireContext())

        recyclerView = view.rvProfileDeActive
        activeAdapter = ProfileDeActiveAdapter(object : IClickListnearProfille {
            override fun clickListener(
                baseID: Int,
                viewAdapter: ItemProfileModelsBinding,
                isLike: Boolean,
                position: Int
            ) {
                if (!isLike) {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                } else {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                }
                mHomeViewModel.postLike("Bearer $TOKEN_USER", baseID)

            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

                if (adver != 3) {
                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
                    intent.putExtra("hello", baseID)
                    startActivity(intent)
                    (activity as AppCompatActivity).overridePendingTransition(
                        R.anim.zoom_enter,
                        R.anim.zoom_exit
                    )

                } else {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
                        startActivity(browserIntent)
                    }catch (e: ActivityNotFoundException){
                        uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

            override fun clickListenerChange(baseID: Int, type: Int) {
                val intent = Intent(requireActivity(), ChangeProductActivity::class.java)
                intent.putExtra("changeProductID", baseID)
                startActivity(intent)
            }

            override fun clickListenerIsActive(baseID: Int) {

                mHomeViewModel.getServices()
                mHomeViewModel.myServices.observe(viewLifecycleOwner) { list ->
                    if (list.isSuccessful) {
                        map.clear()
                        map["payId"] = "1"
                        map["productId"] = baseID.toString()
                        mHomeViewModel.payGenerate("Bearer $TOKEN_USER", map)

                    } else {
                        dialogLoader.dismiss()
                        val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                        alertDialogMessage(
                            jsonObj.getString("message").toString(),
                            jsonObj.getString("errors").toString().replace("[\"", "")
                                .replace("\"]", "")
                        )
                    }
                }

                alertDialogLoader()

            }

            override fun clickListenerButton(baseID: Int) {
                alertDialogCancel(baseID)
            }

        })
        recyclerView.adapter = activeAdapter
        recyclerView.setHasFixedSize(true)



        mHomeViewModel.myPayGenerate.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                dialogLoader.dismiss()
                alertDialogPay(
                    list.body()?.data?.order?.description.toString(),
                    "Ваш тариф составляет " +
                            "${list.body()?.data?.order?.price} ${list.body()?.data?.order?.currency} за ${list.body()?.data?.order?.term} дней",
                    list.body()?.data?.url.toString()
                )
            } else {
                dialogLoader.dismiss()
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(), " mm "
//                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }
        }


        return view.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            mHomeViewModel.getShopsOFF("Bearer $TOKEN_USER", AppConstants.ID_SHOP_USER.toString())
            mHomeViewModel.myShopsOFF.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful) {
                    recyclerView.removeAllViewsInLayout()
                    list.body()?.data?.let { activeAdapter.setList(it) }
                } else {
                    uToast(requireContext(), "Active adapter Error")
                }
            }

        } catch (e: ApiException) {
            e.printStackTrace()
        }

    }

    private fun alertDialogLoader() {

        dialogLoader.setCancelable(false)
        dialogLoader.setCanceledOnTouchOutside(false)
        dialogLoader.setContentView(R.layout.dialog_loader)
        dialogLoader.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoader.show()

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
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(requireContext(), browserIntent, null)
            dialogPay.dismiss()
        }
        buttonNOT.setOnClickListener {
            dialogPay.dismiss()
        }
        dialogPay.show()
    }


    private fun alertDialogCancel(idP: Int) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_delete_shop)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes)
        val buttonNO = dialog.findViewById<Button>(R.id.dialog_no)
        val title = dialog.findViewById<TextView>(R.id.txt_descript)
        title.text = resources.getText(R.string.delete_products)

        buttonNO.setOnClickListener {
            dialog.dismiss()
        }
        buttonYES.setOnClickListener {
            mHomeViewModel.postDeleteProduct("Bearer $TOKEN_USER","delete", idP)
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}