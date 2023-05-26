package product.promikz.screens.hometwo.stories.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryUser(
    val username: String,
    val profilePicUrl: String,
    val views: String,
    val idS: String,
    val stories: ArrayList<Story>
) : Parcelable