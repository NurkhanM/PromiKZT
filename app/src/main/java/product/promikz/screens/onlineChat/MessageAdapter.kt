package product.promikz.screens.onlineChat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.inteface.IClickListnearOnlineChat
import product.promikz.R
import product.promikz.models.message.index.Data
import product.promikz.AppConstants.USER_ID
import product.promikz.databinding.ItemMessageIndexBinding
import product.promikz.models.test.countMessageIsView.CountMessageModels


class MessageAdapter(private val mIClickListnear: IClickListnearOnlineChat) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listTovar = ArrayList<Data>()


    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        listTovar.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listTovar.size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMessageIndexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]

//        uLogD("TEST -> position: $position ==  ${currentItem.count}")

        holder.binding.mesName.text = currentItem.product.name
        holder.binding.mesDate.text = currentItem.updated_at
        holder.binding.smsUser.text = currentItem.lastText

        if (currentItem.count != 0){
            holder.binding.countMessageisView.text = currentItem.count.toString()
            holder.binding.countMessageisView.visibility = View.VISIBLE
        }else{
            holder.binding.countMessageisView.visibility = View.GONE
        }

        if (currentItem.user.id == USER_ID){
            Glide.with(context).load(currentItem.product.user.img)
                .thumbnail(Glide.with(context).load(R.drawable.loader2))
                .centerCrop()
                .into(holder.binding.mesPhotoUser)
            holder.binding.mesUser.text = currentItem.product.user.name
        } else{
            Glide.with(context).load(currentItem.user.img)
                .thumbnail(Glide.with(context).load(R.drawable.loader2))
                .centerCrop()
                .into(holder.binding.mesPhotoUser)
            holder.binding.mesUser.text = currentItem.user.name
        }


        Glide.with(context).load(currentItem.product.img)
            .thumbnail(Glide.with(context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.mesProductPhoto)




        holder.binding.rowMessage.setOnClickListener {
            mIClickListnear.clickListener(currentItem.id, currentItem.product.id, currentItem.user.img,
                currentItem.product.user.img, currentItem.user.id)
        }

    }

    override fun getItemCount(): Int {
        return listTovar.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Data>, mesShow: MutableList<CountMessageModels>) {
        listTovar = ArrayList(list) // make a copy of the list

        // update the count property of each corresponding Data object
        for (i in 0 until listTovar.size) {
            val chatId = listTovar[i].id

            // find the matching CountMessageModels object by chat id
            val countModel = mesShow.find { it.id == chatId }

            // update the count property of the Data object
            countModel?.let { count ->
                listTovar[i] = listTovar[i].copy(count = count.countShowMessage, lastText = count.textUserChat)
            }
        }

        // sort the list by count in descending order
        val countComparator = compareByDescending<Data> { it.count }
        val timeComparator = compareByDescending<Data> { it.updated_at }
        val sortedList = listTovar.sortedWith(countComparator.then(timeComparator))


        listTovar = ArrayList(sortedList)

        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemMessageIndexBinding) : RecyclerView.ViewHolder(binding.root)

}
