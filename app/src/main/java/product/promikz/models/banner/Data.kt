package product.promikz.models.banner

data class Data(
    val before_date: String?,
    val created_at: String,
    val id: Int,
    val img: String,
    val link: String?,
    val user: User
)