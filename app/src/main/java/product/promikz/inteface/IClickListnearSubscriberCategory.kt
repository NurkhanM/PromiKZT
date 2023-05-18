package product.promikz.inteface

import product.promikz.databinding.CastomSelectSubscriberCategoryBinding


interface IClickListnearSubscriberCategory {
     fun clickListener(int: Int, name: String, boolean: Boolean)
     fun clickListenerBoolean(boolean: Boolean, viewAdapter: CastomSelectSubscriberCategoryBinding, number: Int)
}