package product.promikz.models.notification.index

data class Report(
    val created_at: String,
    val description: String,
    val id: Int,
    val report_type: String,
    val user: User
)