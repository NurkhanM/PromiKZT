package product.promikz.screens.onlineChat.chatShow

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.MyUtils.uGlide
import product.promikz.databinding.ItemContainerReceivedMessageBinding
import product.promikz.databinding.ItemContainerSendMessageBinding
import product.promikz.inteface.IClickListnearOnlineChat
import product.promikz.models.message.show.Message


class MessageShowAdapter(private val mIClickListnear: IClickListnearOnlineChat) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    private var listTovar = ArrayList<Message>()

    private val viewTypeSent = 1
    private val viewTypeRecived = 2

    private var senderId: String? = null


    class SentMessageViewHolder(val context: Context, private val binding: ItemContainerSendMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: Message) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.time
            uGlide(context, binding.imageProfile, chatMessage.user_1_img)
        }

    }

    class ReceivedMessageViewHolder(val context: Context, private val binding: ItemContainerReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: Message) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.time
            uGlide(context, binding.imageProfile, chatMessage.user_2_img)

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        listTovar.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listTovar.size)
        notifyDataSetChanged()
    }

    @Suppress("UNREACHABLE_CODE")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == viewTypeSent) {
            SentMessageViewHolder(context = parent.context,
                ItemContainerSendMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {

            ReceivedMessageViewHolder(context = parent.context,
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == viewTypeSent) {
            (holder as SentMessageViewHolder).setData(listTovar[position])
        } else {
            (holder as ReceivedMessageViewHolder).setData(listTovar[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return listTovar.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (listTovar[position].user_1_uuid.toString() == senderId) {
            viewTypeSent
        } else {
            viewTypeRecived
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<Message>, senderGetID: String) {
        listTovar = list
        notifyDataSetChanged()
        senderId = senderGetID
    }

}
