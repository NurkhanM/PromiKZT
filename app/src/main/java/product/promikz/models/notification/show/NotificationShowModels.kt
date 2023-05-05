package product.promikz.models.notification.show

data class NotificationShowModels(
    val created_at: String,
    val `data`: Data,
    val id: String,
    val notifiable_id: Int,
    val read_at: String,
    val type: String
)