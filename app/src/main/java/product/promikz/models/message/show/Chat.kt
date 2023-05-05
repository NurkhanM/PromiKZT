package product.promikz.models.message.show

data class Chat(
    val created_at: String,
    val id: Int,
    val product: Product,
    val unReadCount: Int?,
    val user: UserX
)