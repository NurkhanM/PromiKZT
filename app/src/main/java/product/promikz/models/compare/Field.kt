package product.promikz.models.compare

data class Field(
    val created_at: String,
    val description: Any,
    val filterDescription: Any,
    val filterName: Any,
    val filter_status: Int,
    val id: Int,
    val max: String,
    val min: String,
    val name: String,
    val pivot_id: Int,
    val required: Int,
    val standard: Any,
    val type: String,
    val value: List<String>,
    val valueUser: String,
    val valueUserNotRefact: String
)