package product.promikz.models.message.show

data class Message(
    val chat_uuid: Int,
    val message: String,
    val time: String,
    val user_1_isView: Int,
    val user_1_uuid: Int,
    val user_2_isView: Int,
    val user_1_img: String,
    val user_2_img: String
//    val user_2_uuid: Int
)