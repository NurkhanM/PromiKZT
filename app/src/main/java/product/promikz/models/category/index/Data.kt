package product.promikz.models.category.index

data class Data(
    val children: List<Children>?,
    val id: Int,
    val parent_id: Int?,
    val name: String?,
    var isSubscriber: Boolean
)