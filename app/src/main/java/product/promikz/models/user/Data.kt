package product.promikz.models.user

data class Data(
    val country: Country,
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val id: Int?,
    val img: String,
    val name: String,
    val phone: String,
    val tokenNotify: String?,
    val phone_verified_at: String,
    val roles: List<Any>,
    val shop: Shop,
    val specialist: Specialist,
    val status: String,
    val unreadNotifications: Int?,
    val type: String
)