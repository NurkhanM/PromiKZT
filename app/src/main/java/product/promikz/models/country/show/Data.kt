package product.promikz.models.country.show

data class Data(
    val children: List<Children>,
    val created_at: String,
    val id: Int,
    val name: String,
    val parent: Any,
    val parent_id: Any
)