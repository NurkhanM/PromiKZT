package product.promikz.models.review.post

data class Data(
    val created_at: String,
    val id: Int,
//    val rating: Any,
    val review_type: String,
    val text: String,
    val user: User
)