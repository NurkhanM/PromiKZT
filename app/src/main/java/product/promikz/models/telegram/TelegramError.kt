package product.promikz.models.telegram

data class TelegramError(
    val description: String,
    val error_code: Int,
    val ok: Boolean
)