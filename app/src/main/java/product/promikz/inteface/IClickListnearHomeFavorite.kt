package product.promikz.inteface

import product.promikz.databinding.ItemMakeupModelsBinding

interface IClickListnearHomeFavorite {
    fun clickListener(baseID: Int, viewAdapter: ItemMakeupModelsBinding, isLike: Boolean, position: Int)
    fun clickListener2(baseID: Int, adver: Int, favorite: String)
}