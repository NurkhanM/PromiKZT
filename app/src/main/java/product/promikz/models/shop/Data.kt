package product.promikz.models.shop

data class Data(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val name: String,
    val status: Int,
    val user: User,
    val verified: Int
)
