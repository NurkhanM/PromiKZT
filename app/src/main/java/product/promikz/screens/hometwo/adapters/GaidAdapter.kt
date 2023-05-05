package product.promikz.screens.hometwo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.R
import product.promikz.databinding.ItemGaidBinding
import product.promikz.models.test.storeGaid.GaidModels
import java.util.*

class GaidAdapter(private val mIClickListnear: IClickListnearHomeFavorite) :
    RecyclerView.Adapter<GaidAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listTovar = ArrayList<GaidModels>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGaidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]


        holder.binding.textGaid.text = currentItem.name

        Glide.with(context).load(listTovar[position].image)
            .thumbnail(Glide.with(context).load(R.drawable.loader2))
            .centerCrop()
            .into(holder.binding.imageGaid)

        holder.binding.rowCostom.setOnClickListener {
            mIClickListnear.clickListener2(position, 2, "false")
        }


    }

    override fun getItemCount(): Int {
        return listTovar.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<GaidModels>) {
        listTovar = list as ArrayList<GaidModels>
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemGaidBinding) : RecyclerView.ViewHolder(binding.root)

}

