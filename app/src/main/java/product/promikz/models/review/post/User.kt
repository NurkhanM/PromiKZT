package product.promikz.models.review.post

data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int,
    val img: String,
    val name: String,
    val phone: String,
    val phone_verified_at: String,
    val status: Int,
//    val tokenNotify: Any,
    val type: Int,
    val unreadNotifications: Int
)