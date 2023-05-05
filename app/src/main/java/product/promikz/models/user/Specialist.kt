package product.promikz.models.user

data class Specialist(
    val categories: List<Category>,
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
    val skills: List<Skill>,
    val specializations: List<Specialization>,
    val status: Int,
    val updated_at: String
)