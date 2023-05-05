package product.promikz.models.post.shopCreate

data class Data(
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