package product.promikz.models.products.show

data class City(
    val created_at: String,
    val id: Int,
    val name: String,
    val parent: Parent,
    val parent_id: Int
)