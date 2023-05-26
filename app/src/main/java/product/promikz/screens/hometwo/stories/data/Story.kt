package product.promikz.screens.hometwo.stories.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val url: String,
    val link: String,
    val storyDate: String
    ) : Parcelable {

    fun isVideo() = url.contains(".mp4")
}