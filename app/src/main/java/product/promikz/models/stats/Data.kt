package product.promikz.models.stats

data class Data(
    val price: Price,
    val categoryMain: CategoryMain,
    val totalCountAdvertising: Int,
    val totalCountBrand: Int,
    val totalCountCategory: Int,
    val totalCountCountry: Int,
    val totalCountProduct: Int,
    val totalCountSchool: Int,
    val totalCountSpecialist: Int,
    val totalCountTradeIn: Int,
    val totalCountUser: Int
)