package product.promikz.screens.onlineChat.chatShow

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_ID
import product.promikz.MyUtils
import product.promikz.MyUtils.uLogD
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentChatShowBinding
import product.promikz.inteface.IClickListnearOnlineChat
import product.promikz.models.message.show.Message
import product.promikz.screens.update.UpdateActivity
import product.promikz.viewModels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChatShowFragment : Fragment() {

    private var _binding: FragmentChatShowBinding? = null
    private val binding get() = _binding!!

    private val newMessageMap = HashMap<String, String>()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MessageShowAdapter

    private lateinit var mOnlineChat: HomeViewModel

    private var mesShow = ArrayList<Message>()

    private var idShat: Int = -1
    private var idProductShat: Int = -1
    private var userImg1: String = ""
    private var userImg2: String = ""

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listenerRegistration2: ListenerRegistration

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mOnlineChat = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentChatShowBinding.inflate(inflater, container, false)
        val view = binding

        val db = (activity as ChatShowActivity).database

        val arguments = (activity as AppCompatActivity).intent.extras
        idShat = arguments!!["idChat"] as Int
        idProductShat = arguments["idProductChat"] as Int
        userImg1 = arguments["imgUser1"] as String
        userImg2 = arguments["imgUser2"] as String

        mOnlineChat.getMessageShow("Bearer $TOKEN_USER", idShat)


        listenerRegistration = db.collection("chat")
            .whereEqualTo("chat_uuid", idShat)
            .whereEqualTo("user_2_uuid", USER_ID)
            .whereEqualTo("user_2_isView", 0)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }
//                uLogD("TEST -> document id == $docId ")
                value?.documents?.forEach { doc ->
                    val docId = doc.id // получение идентификатора документа Firestore
                    uLogD("TEST -> document id == $docId, chat_uuid == ${doc.get("chat_uuid")}, user_2_uuid == ${doc.get("user_2_uuid")}, user_2_isView == ${doc.get("user_2_isView")}")
                    db.collection("chat").document(docId).update(
                        mapOf(
                            "user_2_isView" to 1
                        )
                    )
                }

            }





        listenerRegistration2 = db.collection("chat")
            .whereEqualTo("chat_uuid", idShat)
//            .orderBy("time", Query.Direction.DESCENDING)
            .orderBy("time")
            .addSnapshotListener(EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@EventListener
                }
                if (value != null) {

                    value.documentChanges.forEach { dc ->


                        if (dc.type == DocumentChange.Type.ADDED) {

                            mesShow.add(
                                Message(
                                    dc.document.get("chat_uuid").toString().toInt(),
                                    dc.document.get("message").toString(),
                                    getReadableDateTime(dc.document.getDate("time")!!),
                                    dc.document.get("user_1_isView").toString().toInt(),
                                    dc.document.get("user_1_uuid").toString().toInt(),
                                    dc.document.get("user_2_isView").toString().toInt(),
                                    userImg1,
                                    userImg2
                                )
                            )
                        }


                    }



                    adapter.setList(mesShow, USER_ID.toString())
                    adapter.notifyDataSetChanged()
                    recyclerView.smoothScrollToPosition(mesShow.size - 1)

                }
            })




        view.btnPostMessage.setOnClickListener {

            newMessageMap["text"] = view.edtNewMessage.text.toString()
            newMessageMap["product_id"] = idProductShat.toString()
            newMessageMap["chat_id"] = idShat.toString()

            mOnlineChat.newMessage(
                "Bearer $TOKEN_USER",
                newMessageMap
            )
            view.edtNewMessage.text.clear()
            newMessageMap.clear()
            binding.root.hideKeyboard()

        }

        recyclerView = view.rvMessageShow
        adapter = MessageShowAdapter(object : IClickListnearOnlineChat {

            override fun clickListener(
                baseID: Int,
                proChatID: Int,
                userImg1: String,
                userImg2: String,
                userID: Int
            ) {
                //todo
            }

        })

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        mOnlineChat.myMessageShow.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                view.textProduct.text = list.body()?.data?.chat?.product?.name
                MyUtils.uGlide(
                    requireContext(),
                    view.imgProduct,
                    list.body()?.data?.chat?.product?.img
                )
                view.nextProduct.visibility = View.VISIBLE
            } else {
                uToast(requireContext(), "Error")
            }
        }


        view.nextProduct.setOnClickListener {
            val intent = Intent(requireActivity(), UpdateActivity::class.java)
            intent.putExtra("hello", idProductShat)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }


        view.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return view.root
    }


    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }


    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
        listenerRegistration2.remove()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}