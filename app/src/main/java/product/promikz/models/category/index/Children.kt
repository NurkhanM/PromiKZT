package product.promikz.models.category.index

data class Children(
    val children: List<ChildrenX>,
    val created_at: String,
    val description: String,
    val follower: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Int,
    val slug: String
)