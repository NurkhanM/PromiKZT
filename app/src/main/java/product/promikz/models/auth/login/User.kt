package product.promikz.models.auth.login

data class User(
    val country: Country,
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int,
    val unreadNotifications: Int?,
    val img: String,
    val name: String,
    val phone: String,
    val phone_verified_at: String,
    val roles: List<Any>,
    val shop: Shop,
    val specialist: Specialist,
    val status: String,
    val type: String
)