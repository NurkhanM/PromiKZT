package product.promikz.screens.hometwo.story

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.inteface.IClickListnearHomeStory
import product.promikz.models.story.index.Data
import product.promikz.MyUtils
import product.promikz.databinding.ItemHomeTwoBinding

class HomeTwoAdapter(private val mIClickListnearHomeStory: IClickListnearHomeStory) :
    RecyclerView.Adapter<HomeTwoAdapter.MyViewHolder>() {
    lateinit var context: Context

    private var listHome = emptyList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHomeTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listHome[position]

        MyUtils.uGlide(context, holder.binding.imageHomeTwo, currentItem.img)
        holder.binding.nameHome2Item.text = currentItem.name
        holder.binding.descHome2Item.text = currentItem.description

        holder.binding.rowHomeTwo.setOnClickListener {
            mIClickListnearHomeStory.clickListener(currentItem.id, currentItem.views.toString())
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

    inner class MyViewHolder(val binding: ItemHomeTwoBinding) : RecyclerView.ViewHolder(binding.root)
}