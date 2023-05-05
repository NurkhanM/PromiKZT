package product.promikz.viewModels

import androidx.lifecycle.*
import product.promikz.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.auth.login.LoginModels
import product.promikz.models.auth.register.RegisterModels
import product.promikz.models.post.shopCreate.ShopCreateM
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    private val repo = Repository()

    private val myList = MutableLiveData<HashMap<String, String>>()



    val getPushPost: LiveData<Response<LoginModels>> =
        myList.switchMap {
            repo.pushPostRepository(it)
        }

    fun setPushPost(params: HashMap<String, String>) {
        viewModelScope.launch {
            myList.postValue(params)
        }
    }

    val myRegisterList: MutableLiveData<Response<RegisterModels>> = MutableLiveData()
    val myFastRegister: MutableLiveData<Response<RegisterModels>> = MutableLiveData()
    val myShopCreate: MutableLiveData<Response<ShopCreateM>> = MutableLiveData()
    val myUpdateShopCreate: MutableLiveData<Response<ShopCreateM>> = MutableLiveData()


    fun pushRegist(
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
        password_confirmation: RequestBody,
        country_id: RequestBody,
        type: RequestBody,
        phone: RequestBody,
        img: MultipartBody.Part?,
    ) {
        viewModelScope.launch {
            myRegisterList.value = repo.pushRegistPost(
                name,
                email,
                password,
                password_confirmation,
                country_id,
                type,
                phone,
                img
            )
        }
    }

    fun fastRegister(
        phone: RequestBody,
        typeRegister: RequestBody,
    ) {
        viewModelScope.launch {
            myFastRegister.value = repo.fastRegisterRepository(
                phone,
                typeRegister,
            )
        }
    }

    fun pushShopCreate(
        auth: String,
        name: RequestBody,
        description: RequestBody,
        img: MultipartBody.Part?,
    ) {
        viewModelScope.launch {
            myShopCreate.value = repo.pushShopCreate(
                auth,
                name,
                description,
                img
            )
        }
    }

    fun updateShopCreate(
        auth: String,
        method: RequestBody,
        idShop: RequestBody,
        name: RequestBody,
        description: RequestBody,
        img: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            myUpdateShopCreate.value = repo.updateShopCreateRepository(
                auth,
                method,
                idShop,
                name,
                description,
                img
            )
        }
    }
}