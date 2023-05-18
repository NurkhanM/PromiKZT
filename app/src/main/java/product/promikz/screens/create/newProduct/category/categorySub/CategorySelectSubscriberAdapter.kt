package product.promikz.screens.create.newProduct.category.categorySub

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.R
import product.promikz.inteface.IClickListnearShops
import product.promikz.databinding.CastomSelectCategoryBinding
import product.promikz.databinding.CastomSelectSubscriberCategoryBinding
import product.promikz.inteface.IClickListnearSubscriberCategory
import product.promikz.models.category.index.Data

class CategorySelectSubscriberAdapter(private val onClickListener: IClickListnearSubscriberCategory) :
    RecyclerView.Adapter<CategorySelectSubscriberAdapter.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CastomSelectSubscriberCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        if (currentItem.isSubscriber){
            holder.binding.imgSubscriber.setImageResource(R.drawable.ic_star2)
        }else{
            holder.binding.imgSubscriber.setImageResource(R.drawable.ic_star)
        }

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

        holder.binding.imgSubscriber.setOnClickListener {
            onClickListener.clickListenerBoolean(currentItem.isSubscriber, holder.binding, currentItem.id)
            currentItem.isSubscriber = !currentItem.isSubscriber
            notifyItemChanged(position)
        }


    }

    inner class MyViewHolder(val binding: CastomSelectSubscriberCategoryBinding) : RecyclerView.ViewHolder(binding.root)

}