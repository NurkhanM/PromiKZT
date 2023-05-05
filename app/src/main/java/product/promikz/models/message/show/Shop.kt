package product.promikz.models.message.show

data class Shop(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val isRating: Float?,
    val name: String,
    val ratingsAvg: Int,
    val status: Int,
    val verified: Int
)