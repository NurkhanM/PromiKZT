package product.promikz.models.review.get.products

data class Data(
    val created_at: String,
    val id: Int,
    val rating: String,
    val review_type: String,
    val text: String,
    val user: User
)