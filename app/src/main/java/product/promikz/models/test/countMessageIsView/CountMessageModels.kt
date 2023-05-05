package product.promikz.models.test.countMessageIsView

data class CountMessageModels(
    var id: Int = 0,
    var countShowMessage: Int = 0,
    var textUserChat: String = ""
) {
    constructor() : this(0, 0, "")
}
