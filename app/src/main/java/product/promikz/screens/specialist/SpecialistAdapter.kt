package product.promikz.screens.specialist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gun0912.tedimagepicker.util.ToastUtil
import product.promikz.R
import product.promikz.databinding.ItemSpecialistModelsBinding
import product.promikz.inteface.IClickListnearHomeSpecialist
import product.promikz.models.specialist.index.Data
import java.util.*
import kotlin.collections.ArrayList

class SpecialistAdapter(private val mIClickListnear: IClickListnearHomeSpecialist) :
    RecyclerView.Adapter<SpecialistAdapter.MyViewHolder>() {
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
        val binding = ItemSpecialistModelsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]

        holder.binding.itemStateTop.visibility = View.GONE
        holder.binding.itemVip.visibility = View.GONE
        holder.binding.itemStateVerified.visibility = View.GONE
        holder.binding.itemStateHot.visibility = View.GONE

        holder.binding.itemHomeLike.text = currentItem.likeCount.toString()
        holder.binding.itemExperience.text = currentItem.experience.toString()

        if (currentItem.ratingsAvg != null) {
            holder.binding.itemSpeRating.rating = currentItem.ratingsAvg
        } else {
            holder.binding.itemSpeRating.rating = 0f
        }

        holder.binding.itemSpeName.text = currentItem.user.name
        holder.binding.itemHomeCityName.text = currentItem.city.name

        Glide.with(ToastUtil.context).load(currentItem.img)
            .thumbnail(Glide.with(ToastUtil.context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.itemHomeImages)

//        }

        holder.binding.rowCostom.setOnClickListener {
            mIClickListnear.clickListener(currentItem.id, holder.binding, false, position)
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

    inner class MyViewHolder(val binding: ItemSpecialistModelsBinding) : RecyclerView.ViewHolder(binding.root)

}