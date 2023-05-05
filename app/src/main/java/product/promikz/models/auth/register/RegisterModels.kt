package product.promikz.models.auth.register

data class RegisterModels(
    val expires_at: String,
    val token: String,
    val token_type: String,
    val user: User
)