package product.promikz.inteface

import product.promikz.databinding.ItemSpecialistModelsBinding

interface IClickListnearHomeSpecialist {
    fun clickListener(baseID: Int, viewAdapter: ItemSpecialistModelsBinding, isLike: Boolean, position: Int)
    fun clickListener2(baseID: Int, adver: Int, favorite: String)
}