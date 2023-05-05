package product.promikz.screens.update.autoMessage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.databinding.ItemCategoryFiltersBinding
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.models.my.string.AutoMessageModels

class AutoMessageAdapter(private val mInterface: IClickListnearUpdateImage) :
    RecyclerView.Adapter<AutoMessageAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listHome = emptyList<AutoMessageModels>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCategoryFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = listHome[position]
        holder.binding.catNameFilters.text = currentItem.autoMessage

        holder.binding.cfFirst.setOnClickListener {
            mInterface.clickListener(currentItem.autoMessage, position)
        }

    }

    override fun getItemCount(): Int {
        return listHome.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<AutoMessageModels>) {
        listHome = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemCategoryFiltersBinding) : RecyclerView.ViewHolder(binding.root)


}