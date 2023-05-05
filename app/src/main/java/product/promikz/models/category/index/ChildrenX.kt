package product.promikz.models.category.index

data class ChildrenX(
    val children: List<Any>,
    val created_at: String,
    val description: Any,
    val follower: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Int,
    val slug: String
)