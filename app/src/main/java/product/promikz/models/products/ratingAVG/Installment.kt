package product.promikz.models.products.ratingAVG

data class Installment(
    val created_at: String,
    val first: Double,
    val firstPercent: Int,
    val id: Int,
    val month: Double,
    val price: Int,
    val type: Int
)