package product.promikz.inteface

import androidx.recyclerview.widget.RecyclerView

interface IClickListnearNotification {
    //todo для чего Position
    fun clickListener(baseID: String, position: Int, holder: RecyclerView.ViewHolder)
}