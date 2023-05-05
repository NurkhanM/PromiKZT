package product.promikz.models.category.show

data class Children(
    val brands: List<Brand>,
    val children: List<ChildrenX>,
    val created_at: String,
    val description: String,
    val fields: List<Field>,
    val followers: Any,
    val id: Int,
    val img: String,
    val name: String,
    val parent_id: Int,
    val slug: String
)