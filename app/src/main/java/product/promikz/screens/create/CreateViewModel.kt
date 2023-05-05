package product.promikz.screens.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import product.promikz.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.errors.ErrorModels
import retrofit2.Response

class CreateViewModel : ViewModel() {

    private val repo = Repository()
    var myProductCreate: MutableLiveData<Response<ErrorModels>> = MutableLiveData()


    fun pushProductCreate(
        auth: String,
        params: HashMap<String, RequestBody>,
        fields: HashMap<String, RequestBody>,
        img: MultipartBody.Part?,
        images: List<MultipartBody.Part>,
    ) {
        viewModelScope.launch {
            myProductCreate.value = repo.pushProductCreate(
                auth,
                fields,
                params,
                img,
                images
            )
        }
    }
}