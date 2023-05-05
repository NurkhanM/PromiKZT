package product.promikz.models.post.productCreate

data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int,
    val img: String,
    val name: String,
    val phone: String,
    val shop: ShopX,
    val status: String
)