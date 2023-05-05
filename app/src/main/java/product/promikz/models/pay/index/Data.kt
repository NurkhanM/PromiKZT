package product.promikz.models.pay.index

data class Data(
    val description: String,
    val id: Int,
    val name: String,
    val price: List<String>,
    val term: String,
    val type: List<String>
)