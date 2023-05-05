package product.promikz.inteface

import product.promikz.databinding.ItemHomeViewRatingBinding

interface IClickListnearHomeRating {
    fun clickListener(baseID: Int, viewAdapter: ItemHomeViewRatingBinding, isLike: Boolean, position: Int)
    fun clickListener2(baseID: Int, adver: Int, favorite: String)
}