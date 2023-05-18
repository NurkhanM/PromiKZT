package product.promikz.models.subscriber.category

data class Subscriber(
    val created_at: String,
    val description: Any,
    val follower: Any,
    val id: Int,
    val img: String,
    val isSubscriber: Boolean,
    val name: String,
    val parent_id: Any,
    val slug: String
)