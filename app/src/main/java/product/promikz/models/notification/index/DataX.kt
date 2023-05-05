package product.promikz.models.notification.index

data class DataX(
    val `data`: DataXX,
    val notifiable: Notifiable,
    val report: Report,
    val user: UserX
)