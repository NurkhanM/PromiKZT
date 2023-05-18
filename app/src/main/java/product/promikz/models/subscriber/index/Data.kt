package product.promikz.models.subscriber.index

data class Data(
    val created_at: String,
    val id: Int,
    val subscriber: Subscriber,
    val subscriber_type: String,
    val user: User
)