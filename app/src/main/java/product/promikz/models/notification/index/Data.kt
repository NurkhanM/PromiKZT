package product.promikz.models.notification.index

data class Data(
    val created_at: String,
    val `data`: DataX,
    val id: String,
    val notifiable_id: Int,
    val read_at: String?,
    val type: String
)