package product.promikz.inteface

import android.view.View
import product.promikz.databinding.ItemProfileModelsBinding

interface IClickListnearProfille {
    fun clickListener(baseID: Int, viewAdapter: ItemProfileModelsBinding, isLike: Boolean, position: Int)
    fun clickListener2(baseID: Int, adver: Int, favorite: String)
    fun clickListenerChange(baseID: Int, type: Int)
    fun clickListenerIsActive(baseID: Int)
    fun clickListenerButton(baseID: Int)
}