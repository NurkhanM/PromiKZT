package product.promikz.models.subscriber.shop

data class Subscriber(
    val created_at: String,
    val description: String,
    val id: Int,
    val img: String,
    val isRating: Int,
    val isSubscriber: Boolean,
    val name: String,
    val ratingsAvg: Int,
    val status: Int,
    val verified: Int
)