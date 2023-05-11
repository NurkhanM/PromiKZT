package product.promikz.models.compare

import android.widget.LinearLayout

data class Data(
    val before_date: String,
    val brand: Brand,
    val categories: List<Category>,
    val city: City,
    val created_at: String,
    val description: String,
    val fast: String?,
    val fields: List<FieldX>,
    val info: List<LinearLayout>,
    val hot: Int,
    val id: Int,
    val images: List<Image>,
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
    val ratingsAvg: Double,
    val reportCount: Int,
    val review: Int,
    val reviewCount: Int,
    val shop: Shop,
    val show_fields: Boolean,
    val state: Int,
    val status: Int,
    val top: String?,
    val type: Int,
    val updated_at: String,
    val verified: Int,
    val views: Int,
    val vip_status: String?
)