package product.promikz.inteface

import product.promikz.databinding.ItemSearchSortBinding

interface IClickListnearSearchSort {
    fun clickListener(baseID: Int, baseCategory: Int, baseName: String, viewAdapter: ItemSearchSortBinding)
    fun clickListener2(baseID: Int, adver: Int, favorite: String)
    fun clickListener3(baseID: Int, viewAdapter: ItemSearchSortBinding, isLike: Boolean, position: Int)
}