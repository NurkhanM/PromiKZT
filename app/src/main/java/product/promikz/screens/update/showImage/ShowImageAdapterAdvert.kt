@file:Suppress("DEPRECATION")

package product.promikz.screens.update.showImage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.R
import product.promikz.databinding.ItemShowImageBinding
import product.promikz.models.products.show.Image

class ShowImageAdapterAdvert(private var images: List<Image>) :
    RecyclerView.Adapter<ShowImageAdapterAdvert.MyViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemShowImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


//        MyUtils.uGlide(context, holder.itemView.image_show, images[position].name)
        Glide.with(context).load(images[position].name).placeholder(R.drawable.loader_small).fitCenter().into(holder.binding.imageShow)

    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class MyViewHolder(val binding: ItemShowImageBinding) : RecyclerView.ViewHolder(binding.root)


}