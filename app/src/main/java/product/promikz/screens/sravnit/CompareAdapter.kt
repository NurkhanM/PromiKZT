package product.promikz.screens.sravnit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import product.promikz.MyUtils.uGlide
import product.promikz.R
import product.promikz.databinding.ItemCompareBinding
import product.promikz.inteface.IClickListnearCompare
import product.promikz.models.compare.Data
import kotlin.math.roundToInt


class CompareAdapter(private val iFace: IClickListnearCompare, val context: Context) :
    RecyclerView.Adapter<CompareAdapter.MyViewHolder>() {

    private var listTovar = ArrayList<Data>()


    @SuppressLint("NotifyDataSetChanged")
    fun deleteMyEducations(position: Int) {
        listTovar.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listTovar.size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCompareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        context = parent.context
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listTovar[position]

        uGlide(context, holder.binding.imgView, currentItem.img)
        holder.binding.textName.text = currentItem.name
        if (currentItem.price == 0) {
            holder.binding.textPrice.text = "Договорная"
        } else {
            holder.binding.textPrice.text = "${currentItem.price} тг"
        }

        if (currentItem.state == 1) {
            holder.binding.textState.text = "Новая"
        } else {
            holder.binding.textState.text = "Б/у"
        }
        holder.binding.textCategory.text = currentItem.categories[0].name
        holder.binding.textBrand.text = currentItem.brand.name



        for (fieldInfo in currentItem.info) {
            fieldInfo.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            fieldInfo.orientation = LinearLayout.VERTICAL

            val nameTextView = fieldInfo.getChildAt(0) as TextView
            nameTextView.textSize = 11f
            nameTextView.setTextColor(ContextCompat.getColor(context, R.color.fon1))

            val valueTextView = fieldInfo.getChildAt(1) as TextView
            valueTextView.textSize = 12f
            valueTextView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60.dpToPx()
            )
            valueTextView.gravity = Gravity.CENTER

            holder.binding.layoutFields.addView(fieldInfo)
        }


        holder.binding.deleteItem.setOnClickListener {
            iFace.clickListener(position, currentItem.name, currentItem.id)
        }
    }


    override fun getItemCount(): Int {
        return listTovar.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Data>) {
        listTovar = ArrayList(list.map { data ->
            val info = data.fields.map { field ->
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.HORIZONTAL

                val nameTextView = TextView(context)
                nameTextView.text = field.name
                nameTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val valueTextView = TextView(context)
                valueTextView.text = field.valueUser
                valueTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                linearLayout.addView(nameTextView)
                linearLayout.addView(valueTextView)

                linearLayout
            }
            data.copy(info = info)
        })
        notifyDataSetChanged()
    }


    inner class MyViewHolder(val binding: ItemCompareBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun Int.dpToPx(): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).roundToInt()
    }

}


