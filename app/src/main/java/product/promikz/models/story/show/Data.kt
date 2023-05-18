package product.promikz.models.story.show

data class Data(
    val before_date: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val images: List<Image>,
    val img: String,
    val name: String,
    val status: Any,
    val views: Int
)