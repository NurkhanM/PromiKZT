package product.promikz.models.story.index

import product.promikz.models.story.show.User

data class Data(
    val before_date: String?,
    val created_at: String,
    val description: String,
    val id: Int,
    val images: List<Image>,
    val img: String,
    val views: Int,
    val name: String,
    val user: User,
    val status: Int
)