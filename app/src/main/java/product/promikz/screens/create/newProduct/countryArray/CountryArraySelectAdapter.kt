package product.promikz.screens.create.newProduct.countryArray

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.promikz.databinding.CastomSelectCategory2Binding
import product.promikz.inteface.IClickListnearFIlterCountry
import product.promikz.models.country.index.Data

class CountryArraySelectAdapter(private val onClickListener: IClickListnearFIlterCountry) :
    RecyclerView.Adapter<CountryArraySelectAdapter.MyViewHolder>() {


    private lateinit var context: Context

    private var userList = emptyList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CastomSelectCategory2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                onClickListener.clickListener(currentItem.id, currentItem.name, true)

            }
        } else {
            holder.binding.rowCostomCaSelect.setOnClickListener {
                onClickListener.clickListener(currentItem.id, currentItem.name, false)

            }
        }

        holder.binding.updateCallBtn.setOnClickListener {
            onClickListener.clickListenerFilter(currentItem.id, currentItem.name, false)
        }


    }

    inner class MyViewHolder(val binding: CastomSelectCategory2Binding) : RecyclerView.ViewHolder(binding.root)


}