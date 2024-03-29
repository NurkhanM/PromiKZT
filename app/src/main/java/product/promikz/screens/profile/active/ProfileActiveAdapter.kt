package product.promikz.screens.profile.active

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.R
import product.promikz.AppConstants.ID_SHOP_USER
import product.promikz.databinding.ItemProfileModelsBinding
import product.promikz.inteface.IClickListnearProfille
import product.promikz.models.products.index.Data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileActiveAdapter(private val mIClickListnear: IClickListnearProfille) :
    RecyclerView.Adapter<ProfileActiveAdapter.MyViewHolder>() {
    lateinit var context: Context

    var listTovar = ArrayList<Data>()


    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        listTovar.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listTovar.size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProfileModelsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }


    @SuppressLint("NewApi", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]

        if (ID_SHOP_USER == currentItem.shop.id) {
            holder.binding.linerActive.visibility = View.VISIBLE
        } else {
            holder.binding.linerActive.visibility = View.GONE
        }

        holder.binding.itemProfileActive.visibility = View.GONE



        if (currentItem.top != null) {
            if (getDateTime(currentItem.top)) {
                holder.binding.itemStateTop.visibility = View.VISIBLE
            } else {
                holder.binding.itemStateTop.visibility = View.GONE
            }
        } else {
            holder.binding.itemStateTop.visibility = View.GONE
        }

        if (currentItem.fast != null) {
            if (getDateTime(currentItem.fast)) {
                holder.binding.itemStateFast.visibility = View.VISIBLE
            } else {
                holder.binding.itemStateFast.visibility = View.GONE
            }
        } else {
            holder.binding.itemStateFast.visibility = View.GONE
        }

        if (currentItem.vip_status != null) {

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

        if (currentItem.isLike) {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite2)
        } else {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite)
        }



        if (currentItem.price == 0 ) {
            holder.binding.itemHomePrice.text = "Договорная"
        } else {
            holder.binding.itemHomePrice.text = currentItem.price.toString() + " ₸"
        }

        holder.binding.itemHomeViews.text = currentItem.views.toString()
        holder.binding.itemHomeTitle.text = currentItem.name
        holder.binding.itemBackTo.text = currentItem.created_at
        holder.binding.itemHomeLike.text = currentItem.likeCount.toString()
        holder.binding.textImageSize.text = currentItem.imagesCount.toString()


        Glide.with(context).load(currentItem.img)
            .thumbnail(Glide.with(context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.itemHomeImages)


        if (currentItem.type == 3) {
            holder.binding.itemHomePrice.text = "Реклама!"
            holder.binding.itemHomeTitle.text = "-> ${currentItem.link.toString()}"
            holder.binding.itemFavorite.visibility = View.GONE
            holder.binding.constDateLikeItem.visibility = View.INVISIBLE
            holder.binding.itemProfileChange.visibility = View.GONE
        }else {
            holder.binding.itemFavorite.visibility = View.VISIBLE
            holder.binding.constDateLikeItem.visibility = View.VISIBLE
            holder.binding.itemProfileChange.visibility = View.VISIBLE
        }

        holder.binding.itemFavorite.setOnClickListener {
            mIClickListnear.clickListener(
                currentItem.id,
                holder.binding,
                currentItem.isLike,
                position
            )

            currentItem.isLike = !currentItem.isLike
            notifyItemChanged(position)

        }


        holder.binding.rowCustomProfile.setOnClickListener {
            mIClickListnear.clickListener2(
                currentItem.id,
                currentItem.type,
                currentItem.link.toString()
            )
        }

        holder.binding.itemProfileChange.setOnClickListener {
            currentItem.id.let { it1 -> mIClickListnear.clickListenerChange(it1, currentItem.type) }
        }

        holder.binding.itemProfileDeactive.setOnClickListener {
            currentItem.id.let { it1 -> mIClickListnear.clickListenerIsActive(it1) }
        }

        holder.binding.itemProfileDelete.setOnClickListener {
            currentItem.id.let { it1 -> mIClickListnear.clickListenerButton(it1) }
        }
    }

    override fun getItemCount(): Int {
        return listTovar.size


    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Data>) {
        listTovar = list as ArrayList<Data>
        notifyDataSetChanged()
    }


    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): Boolean {
        //Сегодняшняя дата
        val currentTime: Date = Calendar.getInstance().time
        //Преобразование приходяшее дата
        val dateParse = SimpleDateFormat("dd.MM.yyyy hh:mm").parse(s) as Date
        //возвращаю boolean
        return dateParse.time > currentTime.time
    }

    inner class MyViewHolder(val binding: ItemProfileModelsBinding) : RecyclerView.ViewHolder(binding.root)

}
