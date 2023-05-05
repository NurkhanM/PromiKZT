package product.promikz.models.like.specialist

data class Data(
    val created_at: String,
    val departure: Int,
    val experience: Int,
    val id: Int,
    val img: String,
    val isLike: Boolean,
    val isRating: Float?,
    val isReport: Int?,
    val isReview: String?,
    val likeCount: Int,
    val ratingsAvg: Float?,
    val reportCount: Int,
    val reviewCount: Int,
    val status: Int,
    val updated_at: String
)