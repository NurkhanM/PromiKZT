package product.promikz.screens.notifications.index

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.databinding.ItemNotificationBinding
import product.promikz.inteface.IClickListnearNotification
import product.promikz.models.notification.index.Data

class NotificationAdapter(private val mIClickListnear: IClickListnearNotification) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    private lateinit var context: Context
    private var userList = ArrayList<Data>()

    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, userList.size)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.binding.txtListTitle.text = currentItem.data.data.title
        holder.binding.txtListName.text = currentItem.data.data.text
        holder.binding.notifDate.text = currentItem.created_at
        holder.binding.txtListType.text = isNameNotification(currentItem.type)
        holder.binding.rowCostomFavorite.setOnClickListener {

            mIClickListnear.clickListener(currentItem.id, position)

        }
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

    inner class MyViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

}