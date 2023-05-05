package product.promikz.models.notification.show

data class Data(
    val `data`: DataX,
    val notifiable: Notifiable,
    val report: Report,
    val user: UserX
)