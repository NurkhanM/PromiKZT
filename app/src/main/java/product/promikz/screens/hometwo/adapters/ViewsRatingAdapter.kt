package product.promikz.screens.hometwo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.R
import product.promikz.databinding.ItemHomeViewRatingBinding
import product.promikz.inteface.IClickListnearHomeRating
import product.promikz.models.products.ratingAVG.Data
import java.util.*

class ViewsRatingAdapter(private val mIClickListnear: IClickListnearHomeRating) :
    RecyclerView.Adapter<ViewsRatingAdapter.MyViewHolder>() {
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
        val binding = ItemHomeViewRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]

        if (currentItem.price != 0) {
            holder.binding.itemHomePrice.text = currentItem.price.toString() + " ₸"
        } else {
            holder.binding.itemHomePrice.text = "Договорная"
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

        holder.binding.itemHomeTitle.text = currentItem.name
        holder.binding.textImageSize.text = currentItem.imagesCount.toString()

        Glide.with(context).load(listTovar[position].img)
            .thumbnail(Glide.with(context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.itemHomeImages)


        if (currentItem.type == 3) {
            holder.binding.itemHomePrice.text = "Реклама!"
            holder.binding.itemHomeTitle.text = "-> ${currentItem.link.toString()}"
            holder.binding.imgFavorite.visibility = View.GONE
        }else{
            holder.binding.imgFavorite.visibility = View.VISIBLE
        }


        holder.binding.imgFavorite.setOnClickListener {
            mIClickListnear.clickListener(
                currentItem.id,
                holder.binding,
                currentItem.isLike,
                position
            )
            currentItem.isLike = !currentItem.isLike
            notifyItemChanged(position)
        }


        holder.binding.rowCostom.setOnClickListener {
            mIClickListnear.clickListener2(
                currentItem.id,
                currentItem.type,
                currentItem.link.toString()
            )
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

    inner class MyViewHolder(val binding: ItemHomeViewRatingBinding) : RecyclerView.ViewHolder(binding.root)

}

