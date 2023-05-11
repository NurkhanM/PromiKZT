package product.promikz.models.compare

data class FieldX(
    val created_at: String,
    val description: String,
    val filterDescription: String,
    val filterName: String,
    val filter_status: Int,
    val id: Int,
    val max: String,
    val min: String,
    val name: String,
    val pivot_id: Int,
    val required: Int,
    val standard: String,
    val type: String,
    val value: List<String>,
    val valueUser: String,
    val valueUserNotRefact: String
)