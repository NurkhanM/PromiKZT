package product.promikz.screens.home.categoryfilter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.databinding.ItemCategoryFiltersBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory
import product.promikz.models.category.show.Children

class CategoryFiltersAdapterPlus(private val mIClickListnearHomeStory: IClickListnearHomeFilterCategory) :
    RecyclerView.Adapter<CategoryFiltersAdapterPlus.MyViewHolder>() {
    lateinit var context: Context

    private var listHome = emptyList<Children>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCategoryFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = listHome[position]
        holder.binding.catNameFilters.text = currentItem.name
        holder.binding.cfFirst.setOnClickListener {
            mIClickListnearHomeStory.clickListener(currentItem.id,  currentItem.name, currentItem.children.isEmpty())
        }

    }

    override fun getItemCount(): Int {
        return listHome.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Children>) {
        listHome = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemCategoryFiltersBinding) : RecyclerView.ViewHolder(binding.root)

}