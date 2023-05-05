package product.promikz.models.post.productCreate

data class Category(
    val created_at: String,
    val description: Any,
    val followers: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Int,
    val slug: String
)