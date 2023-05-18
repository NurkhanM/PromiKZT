package product.promikz.screens.subscriber

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.R
import product.promikz.databinding.ItemSubscriberReadedBinding
import product.promikz.inteface.IClickListnearSubscriber
import product.promikz.models.subscriber.index.Data




class SubscriberAdapter(private val mIClickListnear: IClickListnearSubscriber) :
    RecyclerView.Adapter<SubscriberAdapter.MyViewHolder>() {
    private lateinit var context: Context
    private var userList = ArrayList<Data>()

    // ЛИШНИЙ КОД удалить?
    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, userList.size)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(
        val binding: ItemSubscriberReadedBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSubscriberReadedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        if (isNameNotification(currentItem.subscriber_type) == "Category"){
            holder.binding.textType.text = context.resources.getString(R.string.category2)
        }else{
            holder.binding.textType.text = context.resources.getString(R.string.shop)
        }

        holder.binding.tvTitle.text = currentItem.subscriber.name
        holder.binding.tvMessage.text = currentItem.subscriber.description


        holder.itemView.setOnClickListener {
            mIClickListnear.clickListener(
                currentItem.subscriber.id,
                position,
                holder,
                currentItem.subscriber_type,
                currentItem.subscriber.name
            )
        }
        holder.binding.ivDelete.setOnClickListener {
            mIClickListnear.clickListenerOtpiska(
                currentItem.subscriber.id, isNameNotification(currentItem.subscriber_type), position
            )
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

    private fun isNameNotification(str: String): String {
        return when (str) {
            "App\\Models\\Category" -> {
                return "Category"
            }
            "App\\Models\\Shop" -> {
                return "Shop"
            }
            else -> "Error"
        }
    }
}
