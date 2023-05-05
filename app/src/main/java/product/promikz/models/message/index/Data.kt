package product.promikz.models.message.index

data class Data(
    val created_at: String,
    val updated_at: String,
    val id: Int,
    val product: Product,
    val unReadCount: Int,
    val user: User,
    val count: Int,
    val lastText: String
)