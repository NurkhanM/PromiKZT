package product.promikz.models.auth.login

data class LoginModels(
    val expires_at: String,
    val token: String,
    val token_type: String,
    val user: User
)