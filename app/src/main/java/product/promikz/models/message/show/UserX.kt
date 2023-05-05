package product.promikz.models.message.show

data class UserX(
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int,
    val img: String,
    val name: String,
    val phone: String,
    val phone_verified_at: String,
    val status: Int,
    val type: Int
)