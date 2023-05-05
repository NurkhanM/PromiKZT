package product.promikz.screens.notifications.show

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.databinding.FragmentNotificationShowBinding
import product.promikz.viewModels.HomeViewModel

class NotificationShowFragment : Fragment() {

    private var _binding: FragmentNotificationShowBinding? = null
    private val binding get() = _binding!!

    private var idNotif = ""
    private lateinit var vModels: HomeViewModel

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        vModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentNotificationShowBinding.inflate(inflater, container, false)
        val view = binding
        val arguments = (activity as AppCompatActivity).intent.extras
        idNotif = arguments!!["idNotif"] as String


        vModels.showNotification("Bearer $TOKEN_USER", idNotif)
        vModels.myShowNotification.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                view.cons1.visibility = View.VISIBLE
                view.cons2.visibility = View.GONE
                view.reasonComplaint.text = isNameNotification(list.body()?.type.toString())
                view.nameOther.text = list.body()?.data?.user?.name
                view.notifTitle.text = list.body()?.data?.data?.title
                view.notifDescription.text = list.body()?.data?.data?.text
                view.myTextReport.text = "-> ${list.body()?.data?.report?.description}"
                view.notifDate.text = list.body()?.created_at
            }else{
                view.cons1.visibility = View.GONE
                view.cons2.visibility = View.VISIBLE
            }
        }

        view.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return view.root
    }

    private fun isNameNotification(str: String): String {

        return when (str){
            "App\\Notifications\\NotificationUsersNotify" -> {
                return "PromiKZ"
            }
            "App\\Notifications\\Report" -> {
                return "Ответ на жалабу"
            }
            else -> "Error"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}