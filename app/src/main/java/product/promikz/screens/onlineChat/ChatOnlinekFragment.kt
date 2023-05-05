package product.promikz.screens.onlineChat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.inteface.IClickListnearOnlineChat
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.onlineChat.chatShow.ChatShowActivity
import product.promikz.AppConstants.USER_ID
import product.promikz.databinding.FragmentChatOnlinekBinding
import product.promikz.models.test.countMessageIsView.CountMessageModels
import java.util.ArrayList

class ChatOnlinekFragment : Fragment() {

    private var _binding: FragmentChatOnlinekBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MessageAdapter

    private lateinit var mOnlineChat: HomeViewModel

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listenerRegistration2: ListenerRegistration


// test
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mOnlineChat = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentChatOnlinekBinding.inflate(inflater, container, false)
        val view = binding



        recyclerView = view.rvMessage
        adapter = MessageAdapter(object : IClickListnearOnlineChat {
            override fun clickListener(
                baseID: Int,
                proChatID: Int,
                userImg1: String,
                userImg2: String,
                userID: Int
            ) {
                val intent = Intent(requireActivity(), ChatShowActivity::class.java)
                intent.putExtra("idChat", baseID)
                intent.putExtra("idProductChat", proChatID)
                if (userID == USER_ID) {
                    intent.putExtra("imgUser1", userImg1)
                    intent.putExtra("imgUser2", userImg2)
                } else {
                    intent.putExtra("imgUser1", userImg2)
                    intent.putExtra("imgUser2", userImg1)
                }

                startActivity(intent)
            }

        })

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)



        ref()


        view.ochBackCard.setOnClickListener {
            activity?.onBackPressed()
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        return view.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun ref() {
        val db = (activity as ChatOnlineActivity).database
        mOnlineChat.getMessageIndex("Bearer $TOKEN_USER")
        mOnlineChat.myGetMessage.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                if (list.body()?.data?.isNotEmpty() == true) {


                    binding?.rvMessage?.visibility = View.VISIBLE
                    binding?.consMessage?.visibility = View.GONE
                    binding?.consLoader?.visibility = View.GONE





                    // Создать список mesShow
                    val mesShow = mutableListOf<CountMessageModels>()

                    list?.body()?.data?.forEach { chat ->

                        listenerRegistration = db.collection("chat")
                            .whereEqualTo("chat_uuid", chat.id)
                            .whereEqualTo("user_2_uuid", USER_ID)
                            .whereEqualTo("user_2_isView", 0)
                            .addSnapshotListener(EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                                if (error != null) {
                                    return@EventListener
                                }
                                if (value != null) {
                                    val count = value.size()

                                    val query = db.collection("chat")
                                        .whereEqualTo("chat_uuid", chat.id)
                                        .orderBy("time", Query.Direction.DESCENDING)
                                        .limit(1)

                                    listenerRegistration2 = query.addSnapshotListener { snapshot, e ->
                                            if (e != null) {
                                                return@addSnapshotListener
                                            }

                                            val messageLast =
                                                if (snapshot != null && !snapshot.isEmpty) {
                                                    snapshot.documents[0].getString("message") ?: ""
                                                } else {
                                                    ""
                                                }


                                            // Найти элемент в mesShow по chatId и обновить его count
                                            val index = mesShow.indexOfFirst { it.id == chat.id }
                                            if (index != -1) {
                                                val countMessageModel = mesShow[index].copy(
                                                    countShowMessage = count,
                                                    textUserChat = messageLast
                                                )
                                                mesShow[index] = countMessageModel
                                            } else {
                                                mesShow.add(CountMessageModels(chat.id, count, messageLast))
                                            }


                                        adapter.setList(list.body()?.data ?: emptyList(), mesShow)
                                        adapter.notifyDataSetChanged()



                                    }
                                }
                            })

                    }

                } else {
                    binding?.consMessage?.visibility = View.VISIBLE
                    binding?.rvMessage?.visibility = View.GONE
                    binding?.consLoader?.visibility = View.GONE
                }

            } else {
                Toast.makeText(requireContext(), "Error Server", Toast.LENGTH_SHORT).show()
            }


        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
        if (::listenerRegistration2.isInitialized) {
            listenerRegistration2.remove()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}