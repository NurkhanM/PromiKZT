package product.promikz.screens.profile.active

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import product.promikz.MyUtils.uLogD
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentProfileActiveBinding
import product.promikz.databinding.ItemProfileModelsBinding
import product.promikz.inteface.IClickListnearProfille
import product.promikz.screens.changeProduct.ChangeProductActivity
import product.promikz.screens.changeProduct.changeKursy.ChangeKursyActivity

class ProfileActiveFragment : Fragment() {

    private var _binding: FragmentProfileActiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var mHomeViewModel: HomeViewModel

    lateinit var recyclerView: RecyclerView
    private lateinit var activeAdapter: ProfileActiveAdapter

    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentProfileActiveBinding.inflate(inflater, container, false)
        val view = binding

        dialog = Dialog(requireContext())

        recyclerView = view.rvProfileActive

        activeAdapter = ProfileActiveAdapter(object : IClickListnearProfille {
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
                if(type == 2){
                    val intent = Intent(requireActivity(), ChangeKursyActivity::class.java)
                    intent.putExtra("changeProductID", baseID)
                    startActivity(intent)
                }else{
                    val intent = Intent(requireActivity(), ChangeProductActivity::class.java)
                    intent.putExtra("changeProductID", baseID)
                    startActivity(intent)
                }

            }

            override fun clickListenerIsActive(baseID: Int) {
                alertDialogIsActive(baseID)
            }

            override fun clickListenerButton(baseID: Int) {
                alertDialogCancel(baseID)
            }

        })

        recyclerView.adapter = activeAdapter
        recyclerView.setHasFixedSize(true)



        mHomeViewModel.myDeleteProduct.observe(viewLifecycleOwner){ list ->
            if (list.isSuccessful){
                uToast(requireContext(), resources.getText(R.string.deleted_youre_products).toString())
                onResume()
            }else{
                uToast(requireContext(), R.string.server_error_500.toString())
            }
        }

        mHomeViewModel.myDisabledProduct.observe(viewLifecycleOwner){ list ->
            if (list.isSuccessful){
                uToast(requireContext(), resources.getText(R.string.disabled_youre_products).toString())
                onResume()
            }else{
                uToast(requireContext(), R.string.server_error_500.toString())
            }
        }



        return view.root
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            mHomeViewModel.getShopsON("Bearer $TOKEN_USER", ID_SHOP_USER.toString())
            mHomeViewModel.myShopsON.observe(viewLifecycleOwner) { list ->
                if (list.isSuccessful){
                    recyclerView.removeAllViewsInLayout()
                    list.body()?.data?.let { activeAdapter.setList(it) }
                }else{
                    uToast(requireContext(), "Active adapter Error")
                }
            }

        } catch (e: ApiException) {
            e.printStackTrace()
        }

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
            mHomeViewModel.postDisabledProduct("Bearer $TOKEN_USER", idP)
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}