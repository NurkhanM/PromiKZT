package product.promikz.models.pay.generete

data class Order(
    val created_at: String,
    val currency: String,
    val description: String?,
    val term: String?,
    val id: Int,
    val pay_id: Int,
    val price: String,
    val product_id: Int,
    val updated_at: String,
    val user_id: Int
)