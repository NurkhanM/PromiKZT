package product.promikz.screens.home.categoryfilter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.models.category.index.Data
import product.promikz.databinding.ItemCategoryFiltersBinding
import product.promikz.inteface.IClickListnearHomeFilterCategory

class CategoryFiltersAdapter(private val mIClickListnearHomeStory: IClickListnearHomeFilterCategory) :
    RecyclerView.Adapter<CategoryFiltersAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listHome = emptyList<Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCategoryFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = listHome[position]
        holder.binding.catNameFilters.text = currentItem.name
        holder.binding.cfFirst.setOnClickListener {
            mIClickListnearHomeStory.clickListener(currentItem.id,
                currentItem.name.toString(), currentItem.children?.isEmpty() == true
            )
        }

    }

    override fun getItemCount(): Int {
        return listHome.size

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Data>) {
        listHome = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemCategoryFiltersBinding) : RecyclerView.ViewHolder(binding.root)

}