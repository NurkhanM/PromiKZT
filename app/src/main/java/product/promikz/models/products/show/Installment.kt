package product.promikz.models.products.show

data class Installment(
    val created_at: String,
    val firstPercent: Int,
    val id: Int,
    val price: Int,
    val month: Float,
    val first: Float,
    val type: Int
)