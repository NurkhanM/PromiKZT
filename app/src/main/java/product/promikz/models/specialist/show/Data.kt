package product.promikz.models.specialist.show

data class Data(
    val categories: List<Category>,
    val city: City,
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
    val updated_at: String,
    val user: User
)