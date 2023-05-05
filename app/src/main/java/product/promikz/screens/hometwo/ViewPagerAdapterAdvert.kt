package product.promikz.screens.hometwo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import product.promikz.AppConstants.followBannerString
import product.promikz.R
import product.promikz.databinding.ItemCardBinding

@Suppress("DEPRECATION")
class ViewPagerAdapterAdvert(private var images: List<String>) :
    RecyclerView.Adapter<ViewPagerAdapterAdvert.MyViewHolder>() {
    lateinit var context: Context

    inner class MyViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                val position = adapterPosition

                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(followBannerString[position]))
                    startActivity(context, browserIntent, null)
                } catch (e: Exception) {
                    Toast.makeText(context, context.resources.getText(R.string.not_banner), Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(images[position])
            .into(holder.binding.imageAdvert)

    }

    override fun getItemCount(): Int {
        return images.size
    }
}