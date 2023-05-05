package product.promikz.models.errors

import com.google.gson.annotations.SerializedName

data class Errors (
    val brand_id: List<String>,
    val category_id: List<String>,
    val country_id: List<String>,
    val description: List<String>,
    val identity: List<String>,
    val images: List<String>,
    val img: List<String>,
    @SerializedName("installment.success")
    val installment_success: List<String>,
    @SerializedName("installment.firstPercent")
    val installment_firstPercent: List<String>,
    @SerializedName("installment.price")
    val installment_price: List<String>,
    @SerializedName("installment.type")
    val installment_type: List<String>,
    val name: List<String>,
    val price: List<String>,
    val review: List<String>,
    val shop_id: List<String>,
    val state: List<String>,
    val user_id: List<String>
        )