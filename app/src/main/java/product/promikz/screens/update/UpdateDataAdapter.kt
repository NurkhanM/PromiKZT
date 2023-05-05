package product.promikz.screens.update

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.R
import product.promikz.MyUtils
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.databinding.ItemUpdateImageArrayBinding
import product.promikz.models.products.show.Image
import product.promikz.screens.tradeIn.TovarAdapterHomeTradeIn

class UpdateDataAdapter(private val mIClickListnear: IClickListnearUpdateImage) :
    RecyclerView.Adapter<UpdateDataAdapter.MyViewHolder>() {
    lateinit var context: Context
    private var focusedPosition = -1

    private var listImage = emptyList<Image>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemUpdateImageArrayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val currentItemImages = listImage[position]

        MyUtils.uGlide(context, holder.binding.imageArrayUpdate, currentItemImages.name)

        holder.binding.imageArrayUpdate.setOnClickListener {
            mIClickListnear.clickListener(currentItemImages.name, position)
            holder.binding.itemImageFocus.visibility = View.VISIBLE
            if (focusedPosition != position) {
                notifyItemChanged(position)
                focusedPosition = position
                notifyDataSetChanged()
            }
        }

        if (focusedPosition == -1) {
            holder.binding.itemImageFocus.visibility = View.GONE
        } else {
            if (focusedPosition == position) {
                holder.binding.itemImageFocus.visibility = View.VISIBLE
            } else {
                holder.binding.itemImageFocus.visibility = View.GONE
            }
        }


    }

    override fun getItemCount(): Int {
        return listImage.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListImage(list: List<Image>) {
        listImage = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemUpdateImageArrayBinding) : RecyclerView.ViewHolder(binding.root)

}