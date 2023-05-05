package product.promikz.models.products.show

data class Category(
    val created_at: String,
    val description: Any,
    val fields: List<FieldX>,
    val follower: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Any,
    val slug: String
)