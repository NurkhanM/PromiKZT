package product.promikz.models.category.show

data class Data(
    val brands: List<Brand>,
    val children: List<Children>?,
    val created_at: String,
    val description: Any,
    val fields: List<Field>,
    val follower: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Int,
    val slug: String
)