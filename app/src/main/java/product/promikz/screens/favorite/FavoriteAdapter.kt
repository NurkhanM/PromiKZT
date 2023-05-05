package product.promikz.screens.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.R
import product.promikz.models.favorite.Data
import gun0912.tedimagepicker.util.ToastUtil
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFavorite
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FavoriteAdapter(private val mIClickListnear: IClickListnearHomeFavorite) :
    RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

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
        val binding = ItemMakeupModelsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]


        if (currentItem.top?.let { isNull(it) } == true){
            if (getDateTime(currentItem.top)) {
                holder.binding.itemStateTop.visibility = View.VISIBLE
            } else {
                holder.binding.itemStateTop.visibility = View.GONE
            }
        } else {
            holder.binding.itemStateTop.visibility = View.GONE
        }

        if (currentItem.fast?.let { isNull(it) } == true){
            if (getDateTime(currentItem.fast)) {
                holder.binding.itemStateFast.visibility = View.VISIBLE
            } else {
                holder.binding.itemStateFast.visibility = View.GONE
            }
        } else {
            holder.binding.itemStateFast.visibility = View.GONE
        }

        if (currentItem.vip_status?.let { isNull(it) } == true){
            if (getDateTime(currentItem.vip_status)) {
                holder.binding.itemVip.visibility = View.VISIBLE
            } else {
                holder.binding.itemVip.visibility = View.GONE
            }
        } else {
            holder.binding.itemVip.visibility = View.GONE
        }

        if (currentItem.verified == 1) {
            holder.binding.itemStateVerified.visibility = View.VISIBLE
        } else {
            holder.binding.itemStateVerified.visibility = View.GONE
        }


        holder.binding.itemHomeLike.text = currentItem.views.toString()
        holder.binding.itemHomeLike.text = currentItem.likeCount.toString()

        if (currentItem.price == 0) {
            holder.binding.itemHomePrice.text = "Договорная"
        } else {
            holder.binding.itemHomePrice.text = currentItem.price.toString() + " Тг"
        }


        holder.binding.itemHomeTitle.text = currentItem.name
        holder.binding.itemBackTo.text = currentItem.created_at
        holder.binding.textImageSize.text = currentItem.imagesCount.toString()


        Glide.with(ToastUtil.context).load(currentItem.img)
            .thumbnail(Glide.with(ToastUtil.context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.itemHomeImages)

        val isLike: Boolean = run {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite2)
            true
        }


        holder.binding.imgFavorite.setOnClickListener {
            mIClickListnear.clickListener(currentItem.id, holder.binding, isLike, position)
        }

        holder.binding.rowCostom.setOnClickListener {
            mIClickListnear.clickListener2(currentItem.id, currentItem.type, currentItem.link.toString())
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): Boolean {
        //Сегодняшняя дата
        val currentTime: Date = Calendar.getInstance().time
        //Преобразование приходящее дата
        val dateParse = SimpleDateFormat("dd.MM.yyyy HH:mm").parse(s) as Date

        return dateParse.time > currentTime.time // изменено на >
    }

    private fun isNull(any: String): Boolean {
        return any != "null"
    }

    inner class MyViewHolder(val binding: ItemMakeupModelsBinding) : RecyclerView.ViewHolder(binding.root)

}