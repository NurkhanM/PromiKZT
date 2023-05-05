package product.promikz.screens.create.newProduct.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearShops
import product.promikz.databinding.CastomSelectCategoryBinding
import product.promikz.models.category.index.Data

class CategorySelectAdapter(private val onClickListener: IClickListnearShops) :
    RecyclerView.Adapter<CategorySelectAdapter.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CastomSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<Data>) {
        this.userList = user
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.binding.txtSelectCa.text = currentItem.name

        if (currentItem.children?.isNotEmpty() == true) {
            holder.binding.rowCostomCaSelect.setOnClickListener {
                currentItem.id.let { it1 -> currentItem.name?.let { it2 ->
                    onClickListener.clickListener(it1,
                        it2, true)
                } }

            }
        } else {
            holder.binding.rowCostomCaSelect.setOnClickListener {
                currentItem.id.let { it1 -> currentItem.name?.let { it2 ->
                    onClickListener.clickListener(it1,
                        it2, false)
                } }

            }
        }


    }

    inner class MyViewHolder(val binding: CastomSelectCategoryBinding) : RecyclerView.ViewHolder(binding.root)

}