package product.promikz.models.post.report

data class Data(
    val created_at: String,
    val description: String,
    val id: Int,
    val report_type: String,
    val user: User
)