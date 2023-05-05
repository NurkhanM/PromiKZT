package product.promikz.screens.home.categoryfilter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.AppConstants.BRAND_INT_FILTERS_DATA
import product.promikz.inteface.IClickListnearHomeStory
import product.promikz.models.category.show.Brand
import product.promikz.databinding.ItemCategoryFiltersBinding

class BrandFiltersAdapter(private val mIClickListnearHomeStory: IClickListnearHomeStory) :
    RecyclerView.Adapter<BrandFiltersAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listHome = emptyList<Brand>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCategoryFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = listHome[position]
        holder.binding.catNameFilters.text = currentItem.name
        holder.binding.cfFirst.setOnClickListener {
            BRAND_INT_FILTERS_DATA = currentItem.id
            mIClickListnearHomeStory.clickListener(currentItem.id)
        }
    }


    override fun getItemCount(): Int {
        return listHome.size


    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Brand>) {
        listHome = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemCategoryFiltersBinding) : RecyclerView.ViewHolder(binding.root)


}