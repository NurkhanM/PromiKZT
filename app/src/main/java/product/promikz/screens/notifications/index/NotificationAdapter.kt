package product.promikz.screens.notifications.index

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.databinding.ItemNotificationReadedBinding
import product.promikz.databinding.ItemNotificationUnreadBinding
import product.promikz.inteface.IClickListnearNotification
import product.promikz.models.notification.index.Data

class NotificationAdapter(private val mIClickListnear: IClickListnearNotification) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    private var userList = ArrayList<Data>()

    //todo ЛИШНИЙ КОД удалить?
    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, userList.size)
        notifyDataSetChanged()
    }


    inner class MyViewHolderReaded(
        val binding: ItemNotificationReadedBinding,
        viewType: Int,
        context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        val readType = viewType
        var notifType = VIEW_TYPE_PROMI

        fun bind(currentItem: Data) {
            with(binding) {
                tvMessage.text = currentItem.data.data.text
                tvTitle.text = currentItem.data.data.title
                tvDate.text = currentItem.created_at
                val sender = isNameNotification(currentItem.type)
                tvSender.text = sender
                tvIcon.text = sender.substring(0, 1)

                notifType = when (sender) {
                    "PromiKZ" ->  VIEW_TYPE_PROMI
                    "Ответ на жалобу" ->  VIEW_TYPE_REPORT
                    else -> {VIEW_TYPE_PROMI}
                }
            }
        }
    }

    inner class MyViewHolderUnreaded(
        val binding: ItemNotificationUnreadBinding,
        viewType: Int,
        context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        val readType = viewType
        var notifType = VIEW_TYPE_PROMI

        @SuppressLint("UseCompatLoadingForColorStateLists")
        fun bind(currentItem: Data) {
            with(binding) {
                tvMessage.text = currentItem.data.data.text
                tvTitle.text = currentItem.data.data.title
                tvDate.text = currentItem.created_at
                val sender = isNameNotification(currentItem.type)
                tvSender.text = sender
                tvIcon.text = sender.substring(0, 1)
                if (sender == "PromiKZ") {
                    tvIcon.backgroundTintList =
                        context.resources.getColorStateList(product.promikz.R.color.green)
                    notifType = VIEW_TYPE_PROMI
                } else {
                    tvIcon.backgroundTintList =
                        context.resources.getColorStateList(product.promikz.R.color.fon1)
                    notifType = VIEW_TYPE_REPORT
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_UNREADED -> {
                val binding = ItemNotificationUnreadBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MyViewHolderUnreaded(binding, viewType, context)
            }
            VIEW_TYPE_READED -> {
                val binding = ItemNotificationReadedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MyViewHolderReaded(binding, viewType, context)
            }
            else -> throw RuntimeException("Undefined view type")
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<Data>) {
        this.userList = user as ArrayList<Data>
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = userList[position]
        when (holder) {
            is MyViewHolderReaded -> holder.bind(currentItem)
            is MyViewHolderUnreaded -> holder.bind(currentItem)
        }
        // передаю ViewHolder чтобы не было бага, что при открытии уведомления не прочитанного и возврата
        // обратно, он считался непрочитанным
        holder.itemView.setOnClickListener {
            mIClickListnear.clickListener(
                currentItem.id,
                position,
                holder
            )
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (userList[position].read_at == null) VIEW_TYPE_UNREADED else VIEW_TYPE_READED
    }

    private fun isNameNotification(str: String): String {
        return when (str) {
            "App\\Notifications\\NotificationUsersNotify" -> {
                return "PromiKZ"
            }
            "App\\Notifications\\Report" -> {
                return "Ответ на жалобу"
            }
            "App\\Notifications\\SubscribersNotify" -> {
                return "Подписки"
            }
            else -> "Error"
        }
    }

    companion object {
        const val VIEW_TYPE_READED = 1
        const val VIEW_TYPE_UNREADED = 0
        const val VIEW_TYPE_PROMI = 100
        const val VIEW_TYPE_REPORT = 110
    }

}