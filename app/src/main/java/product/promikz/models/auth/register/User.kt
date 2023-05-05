package product.promikz.models.auth.register

data class User(
    val country: Country,
    val created_at: String,
    val email: String,
    val email_verified_at: String?,
    val id: Int,
    val img: String,
    val name: String,
    val phone: String,
    val tokenNotify: String?,
    val typeRegister: Int,
    val phone_verified_at: String?,
    val status: Int?,
    val type: String,
    val unreadNotifications: Int
)