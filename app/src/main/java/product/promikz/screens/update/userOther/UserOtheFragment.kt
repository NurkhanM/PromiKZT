package product.promikz.screens.update.userOther

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import org.json.JSONObject
import product.promikz.AppConstants
import product.promikz.AppConstants.USER_OTHER_ID
import product.promikz.MyUtils
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentUserOtheBinding
import product.promikz.databinding.ItemProfileModelsBinding
import product.promikz.inteface.IClickListnearProfille
import product.promikz.screens.changeProduct.ChangeProductActivity
import product.promikz.screens.complaint.store.ComplaintActivity
import product.promikz.screens.profile.active.ProfileActiveAdapter
import product.promikz.screens.profile.de_active.ProfileDeActiveAdapter
import product.promikz.screens.update.UpdateActivity
import product.promikz.viewModels.HomeViewModel
import java.util.HashMap

class UserOtheFragment : Fragment() {

    private var _binding: FragmentUserOtheBinding? = null
    private val binding get() = _binding!!


    private var shopOtherUser: Int = -1
    private lateinit var mProfileViewModel: HomeViewModel

    private lateinit var recyclerViewActive: RecyclerView
    private lateinit var recyclerViewDeActive: RecyclerView
    private lateinit var adapterActive: ProfileActiveAdapter
    private lateinit var adapterDeActive: ProfileDeActiveAdapter

    lateinit var dialog: Dialog
    private lateinit var dialogPay: Dialog
    lateinit var dialogLoader: Dialog

    private var isMenu: Boolean = true

    val map: HashMap<String, String> = hashMapOf()

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mProfileViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentUserOtheBinding.inflate(inflater, container, false)
        val view = binding

        dialog = Dialog(requireContext())
        dialogPay = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())

        val arguments = (activity as AppCompatActivity).intent.extras
        shopOtherUser = arguments!!["User2_1"] as Int

        mProfileViewModel.getShops(shopOtherUser)
        mProfileViewModel.myShopsModels.observe(viewLifecycleOwner){ list ->

            if (list.isSuccessful){
                view.anotherUserName.text = list.body()?.data?.name
                view.anotherUserPhone.text = list.body()?.data?.user?.phone
                view.anotherUserDescription.text = list.body()?.data?.description

                MyUtils.uGlide(requireContext(), view.anotherUserImage, list.body()?.data?.img)

                view.shopRating.rating = list.body()?.data?.ratingsAvg!!
                view.textRating.text = list.body()?.data?.ratingsAvg.toString()

            }

        }

        recyclerViewActive = view.rvActive

        adapterActive = ProfileActiveAdapter(object : IClickListnearProfille {
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
                mProfileViewModel.postLike("Bearer ${AppConstants.TOKEN_USER}", baseID)

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
                intent.putExtra("changeProduct", baseID)
                startActivity(intent)
            }

            override fun clickListenerIsActive(baseID: Int) {
                alertDialogIsActive(baseID)
            }

            override fun clickListenerButton(baseID: Int) {
                alertDialogCancel(baseID)
            }

        })
        recyclerViewActive.adapter = adapterActive
        recyclerViewActive.setHasFixedSize(true)



        recyclerViewDeActive = view.rvDeActive
        adapterDeActive = ProfileDeActiveAdapter(object : IClickListnearProfille {
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
                mProfileViewModel.postLike("Bearer ${AppConstants.TOKEN_USER}", baseID)

            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

//                if (adver == 0) {
//                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
//                    intent.putExtra("hello", baseID)
//                    startActivity(intent)
//                    (activity as AppCompatActivity).overridePendingTransition(
//                        R.anim.zoom_enter,
//                        R.anim.zoom_exit
//                    )
//
//                } else {
//                    try {
//                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
//                        startActivity(browserIntent)
//                    }catch (e: ActivityNotFoundException){
//                        uToast(requireContext(), resources.getString(R.string.error_link))
//                    }
//                }
            }

            override fun clickListenerChange(baseID: Int, type: Int) {
                val intent = Intent(requireActivity(), ChangeProductActivity::class.java)
                intent.putExtra("changeProduct", baseID)
                startActivity(intent)
            }

            override fun clickListenerIsActive(baseID: Int) {
                mProfileViewModel.getServices()
                mProfileViewModel.myServices.observe(viewLifecycleOwner) { list ->
                    if (list.isSuccessful) {
                        map.clear()
                        map["payId"] = "1"
                        map["productId"] = baseID.toString()
                        mProfileViewModel.payGenerate("Bearer ${AppConstants.TOKEN_USER}", map)

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
        recyclerViewDeActive.adapter = adapterDeActive
        recyclerViewDeActive.setHasFixedSize(true)


        view.linActive.setOnClickListener {
            view.textActive.setTextColor(resources.getColor(R.color.fon1))
            view.viewActive.visibility = View.VISIBLE
            view.activeMode.visibility = View.VISIBLE
            view.textDeActive.setTextColor(resources.getColor(R.color.black3))
            view.viewDeActive.visibility = View.INVISIBLE
            view.deActiveMode.visibility = View.INVISIBLE
        }

        view.linDeActive.setOnClickListener {
            view.textActive.setTextColor(resources.getColor(R.color.black3))
            view.viewActive.visibility = View.INVISIBLE
            view.activeMode.visibility = View.INVISIBLE
            view.textDeActive.setTextColor(resources.getColor(R.color.fon1))
            view.viewDeActive.visibility = View.VISIBLE
            view.deActiveMode.visibility = View.VISIBLE
        }

        view.clickUpdateBackCard.setOnClickListener {

            activity?.onBackPressed()

        }

        view.itemMenu.setOnClickListener {
            if (isMenu) {
                view.showDopMenu.visibility = View.VISIBLE
                isMenu = false
            } else {
                view.showDopMenu.visibility = View.GONE
                isMenu = true
            }
        }

        val resultCode1 = 201
        val resultCode2 = 202
        // We can do the assignment inside onAttach or onCreate
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // There are no request codes

                if (result.resultCode == resultCode1 && result.data != null) {
                    val intent = Intent()
                    activity?.setResult(101, intent)
                    activity?.finish()
                } else if (result.resultCode == resultCode2) {
                    Toast.makeText(requireContext(), "102", Toast.LENGTH_SHORT).show()
                }
            }

        view.nextComplaintProduct.setOnClickListener {
            val intent = Intent(requireActivity(), ComplaintActivity::class.java)
            activityResultLauncher.launch(intent)

            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            view.showDopMenu.visibility = View.GONE
        }

        mProfileViewModel.myDeleteProduct.observe(viewLifecycleOwner){ list ->
            if (list.isSuccessful){
                uToast(requireContext(), resources.getText(R.string.deleted_youre_products).toString())
                onResume()
            }else{
                uToast(requireContext(), R.string.server_error_500.toString())
            }
        }

        mProfileViewModel.myDisabledProduct.observe(viewLifecycleOwner){ list ->
            if (list.isSuccessful){
                uToast(requireContext(), resources.getText(R.string.disabled_youre_products).toString())
                onResume()
            }else{
                uToast(requireContext(), R.string.server_error_500.toString())
            }
        }

        mProfileViewModel.myPayGenerate.observe(viewLifecycleOwner) { list ->
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
            mProfileViewModel.getShopsON("Bearer ${AppConstants.TOKEN_USER}", USER_OTHER_ID.toString())
            mProfileViewModel.myShopsON.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful){
                    list.body()?.data?.let { adapterActive.setList(it) }
                }else{
                    uToast(requireContext(), "Active adapter Error")
                }
            }
            mProfileViewModel.getShopsOFF("Bearer ${AppConstants.TOKEN_USER}", USER_OTHER_ID.toString())
            mProfileViewModel.myShopsOFF.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful){
                    list.body()?.data?.let { adapterDeActive.setList(it) }
                }else{
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
            mProfileViewModel.postDeleteProduct("Bearer ${AppConstants.TOKEN_USER}","delete", idP)
            dialog.dismiss()
        }

        dialog.show()

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

    private fun alertDialogIsActive(idP: Int) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_delete_shop)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes)
        val buttonNO = dialog.findViewById<Button>(R.id.dialog_no)
        val title = dialog.findViewById<TextView>(R.id.txt_descript)
        title.text = "Вы хотите деактивировать этот продукт"

        buttonNO.setOnClickListener {
            dialog.dismiss()
        }
        buttonYES.setOnClickListener {
            mProfileViewModel.postDisabledProduct("Bearer ${AppConstants.TOKEN_USER}", idP)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}