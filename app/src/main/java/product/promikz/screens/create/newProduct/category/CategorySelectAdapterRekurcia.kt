package product.promikz.screens.create.newProduct.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearShops
import product.promikz.models.category.show.Children
import product.promikz.databinding.CastomSelectCategoryBinding

class CategorySelectAdapterRekurcia(private val onClickListener: IClickListnearShops) :
    RecyclerView.Adapter<CategorySelectAdapterRekurcia.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Children>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CastomSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<Children>) {
        this.userList = user
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]


        if (currentItem.children.isNotEmpty()) {
            holder.binding.txtSelectCa.text = currentItem.name
            holder.binding.rowCostomCaSelect.setOnClickListener {
                onClickListener.clickListener(currentItem.id, currentItem.name, true)
            }
        } else {

            holder.binding.txtSelectCa.text = currentItem.name
            holder.binding.rowCostomCaSelect.setOnClickListener {
                onClickListener.clickListener(currentItem.id, currentItem.name, false)

            }
        }
    }
    inner class MyViewHolder(val binding: CastomSelectCategoryBinding) : RecyclerView.ViewHolder(binding.root)

}