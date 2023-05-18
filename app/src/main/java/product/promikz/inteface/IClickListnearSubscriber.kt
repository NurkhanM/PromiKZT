package product.promikz.inteface

import androidx.recyclerview.widget.RecyclerView

interface IClickListnearSubscriber {
    //todo для чего Position
    fun clickListener(baseID: Int, position: Int, holder: RecyclerView.ViewHolder, type: String, name: String)
    fun clickListenerOtpiska(baseID: Int, name: String, position: Int)
}