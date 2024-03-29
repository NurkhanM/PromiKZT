package product.promikz.models.products.show

data class Data(
    val before_date: String?,
    val brand: Brand,
    val categories: List<Category>,
    val city: City,
    val created_at: String,
    val description: String,
    val fast: String?,
    val fields: List<Field>,
    val hot: Int,
    val id: Int,
    val images: List<Image>,
    val imagesCount: Int,
    val img: String?,
    val installment: Installment?,
    val show_fields: Boolean,
    val isLike: Boolean,
    val isRating: Float?,
    val isReport: Int?,
    val isReview: Boolean,
    val likeCount: Int,
    val link: String?,
    val myProduct: Int,
    val name: String,
    val price: Int,
    val ratingsAvg: Float,
    val reportCount: Int,
    val review: Int,
    val reviewCount: Int,
    val shop: Shop,
    val state: Int,
    val status: Int,
    val top: String?,
    val type: Int,
    val updated_at: String,
    val user: User,
    val verified: Int,
    val views: Int,
    val vip_status: String?
)