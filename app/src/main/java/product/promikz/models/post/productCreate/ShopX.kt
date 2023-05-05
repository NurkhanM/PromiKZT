package product.promikz.models.post.productCreate

data class ShopX(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val name: String,
    val status: Int,
    val verified: Int
)