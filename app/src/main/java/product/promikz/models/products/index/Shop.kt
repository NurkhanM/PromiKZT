package product.promikz.models.products.index

data class Shop(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val name: String,
    val status: Int,
    val verified: Int
)