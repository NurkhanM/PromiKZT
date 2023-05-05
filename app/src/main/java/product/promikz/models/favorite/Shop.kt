package product.promikz.models.favorite

data class Shop(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val name: String,
    val status: Int,
    val verified: Int
)