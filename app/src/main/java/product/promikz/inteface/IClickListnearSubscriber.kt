package product.promikz.inteface

import androidx.recyclerview.widget.RecyclerView

interface IClickListnearSubscriber {
    //todo для чего Position
    fun clickListener(baseID: String, position: Int, holder: RecyclerView.ViewHolder)
    fun clickListenerOtpiska(baseID: Int, name: String, position: Int)
}