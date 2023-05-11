package product.promikz.models.compare

data class Shop(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val isRating: Int,
    val name: String,
    val ratingsAvg: Int,
    val status: Int,
    val verified: Int
)