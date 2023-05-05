package product.promikz.models.favorite

data class Data(
    val before_date: String?,
    val created_at: String,
    val description: String,
    val fast: String?,
    val hot: Int,
    val id: Int,
    val imagesCount: Int,
    val img: String,
    val isLike: Boolean,
    val isRating: Float?,
    val isReport: Int?,
    val isReview: String?,
    val likeCount: Int,
    val link: String?,
    val myProduct: Int,
    val name: String,
    val price: Int,
    val ratingsAvg: Float?,
    val reportCount: Int,
    val review: Int,
    val reviewCount: Int,
    val shop: Shop,
    val state: Int,
    val status: Int,
    val top: String?,
    val type: Int,
    val updated_at: String,
    val verified: Int,
    val views: Int,
    val vip_status: String?
)