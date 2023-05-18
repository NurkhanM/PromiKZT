package product.promikz.models.subscriber.index

data class Subscriber(
    val created_at: String,
    val description: String,
    val follower: Any,
    val id: Int,
    val img: String,
    val isRating: Int,
    val isSubscriber: Boolean,
    val name: String,
    val parent_id: Any,
    val ratingsAvg: Int,
    val slug: String,
    val status: Int,
    val verified: Int
)