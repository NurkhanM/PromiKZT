package product.promikz.screens.create.newProduct.brand

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearShops
import product.promikz.models.category.show.Brand
import product.promikz.databinding.CastomSelectCategoryBinding

class BrandSelectAdapter(private val onClickListener: IClickListnearShops) :
    RecyclerView.Adapter<BrandSelectAdapter.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Brand>()


    class MyViewHolder(private val binding: CastomSelectCategoryBinding, private val onClickListener: IClickListnearShops) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: Brand) {
            binding.txtSelectCa.text = currentItem.name
            binding.rowCostomCaSelect.setOnClickListener {
                onClickListener.clickListener(currentItem.id, currentItem.name, true)
            }
        }
        //todo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context
        val binding = CastomSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onClickListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.bind(currentItem)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<Brand>) {
        this.userList = user
        notifyDataSetChanged()
    }
}