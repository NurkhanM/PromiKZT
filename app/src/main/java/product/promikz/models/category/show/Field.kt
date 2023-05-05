package product.promikz.models.category.show

import product.promikz.models.products.show.FieldX

data class Field(
    val created_at: String,
    val description: String,
    val filterDescription: Any,
    val filterName: Any,
    val `field`: FieldX,
    val filter_status: Int,
    val id: Int,
    val max: Int?,
    val min: Int?,
    val name: String,
    val pivot_id: Int,
    val required: Int,
    val standard: String,
    val type: String,
    val value: List<String>
)