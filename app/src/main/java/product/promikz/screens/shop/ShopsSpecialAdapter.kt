package product.promikz.screens.shop

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearShops
import product.promikz.models.review.get.specialist.Data
import product.promikz.MyUtils
import product.promikz.databinding.CastomRowBinding

class ShopsSpecialAdapter(private val onClickListener: IClickListnearShops) :
    RecyclerView.Adapter<ShopsSpecialAdapter.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CastomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        MyUtils.uGlide(context, holder.binding.imgItemHome, currentItem.user.img)
        holder.binding.txtListName.text = currentItem.user.name
        holder.binding.txtListEmail.text = currentItem.user.email
        holder.binding.txtListDescrip.text = currentItem.text

        holder.binding.rowCostomFavorite.setOnClickListener {

        }

    }

    inner class MyViewHolder(val binding: CastomRowBinding) : RecyclerView.ViewHolder(binding.root)

}