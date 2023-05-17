package product.promikz.viewModels

import androidx.lifecycle.*
import product.promikz.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.auth.email.one.ActivitedSuccessModels
import product.promikz.models.auth.login.LoginModels
import product.promikz.models.banner.BannerIndexModels
import product.promikz.models.errors.ErrorModels
import product.promikz.models.errors.Errors
import product.promikz.models.specialization.SpecializationModels
import product.promikz.models.like.product.LikeProductModels
import product.promikz.models.like.specialist.LikeSpecialistModels
import product.promikz.models.pay.generete.PayGenereteModels
import product.promikz.models.pay.index.PayModels
import product.promikz.models.post.report.ReportModels
import product.promikz.models.stats.StateModels
import product.promikz.models.category.index.CategoryIndexModels
import product.promikz.models.category.show.CategoryShowModels
import product.promikz.models.compare.CompareModels
import product.promikz.models.country.index.CountryIndexModels
import product.promikz.models.country.show.CountryShowModels
import product.promikz.models.favorite.FavoriteModels
import product.promikz.models.message.index.MessageIndexModels
import product.promikz.models.message.show.MessageShowModels
import product.promikz.models.notification.index.NotificationModels
import product.promikz.models.notification.show.NotificationShowModels
import product.promikz.models.post.message.store.NewMessageModels
import product.promikz.models.products.index.ProductIndexModels
import product.promikz.models.products.ratingAVG.RatingAVGModels
import product.promikz.models.products.show.ProductShowModels
import product.promikz.models.review.get.products.ReviewProductModels
import product.promikz.models.review.get.specialist.ReviewSpecialistModels
import product.promikz.models.review.post.ReviewPostModels
import product.promikz.models.shop.ShopsModels
import product.promikz.models.similar.SimilarModels
import product.promikz.models.specialist.index.SpecialistIndexModels
import product.promikz.models.specialist.show.SpecialistShowModels
import product.promikz.models.specialist.skills.SkillsModels
import product.promikz.models.story.index.StoryIndex
import product.promikz.models.user.UserModels
import product.promikz.models.version.VersionModels
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val repo = Repository()

    val myStats: MutableLiveData<Response<StateModels>> = MutableLiveData()
    val myVersion: MutableLiveData<Response<VersionModels>> = MutableLiveData()
    val myServices: MutableLiveData<Response<PayModels>> = MutableLiveData()
    val myPayGenerate: MutableLiveData<Response<PayGenereteModels>> = MutableLiveData()
    val myPayGenerateBanner: MutableLiveData<Response<PayGenereteModels>> = MutableLiveData()
    val myPayGenerateStory: MutableLiveData<Response<PayGenereteModels>> = MutableLiveData()
    val myStory: MutableLiveData<Response<StoryIndex>> = MutableLiveData()
    private val myBanner = MutableLiveData<String?>()
    val myShowProducts: MutableLiveData<Response<ProductShowModels>> = MutableLiveData()
    val myShopsON: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myShopsOFF: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myDeleteProduct: MutableLiveData<Response<String>> = MutableLiveData()
    val myDisabledProduct: MutableLiveData<Response<String>> = MutableLiveData()
    val myDeleteShop: MutableLiveData<Response<String>> = MutableLiveData()
    val myFilterProducts: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myFilterProductsEND: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myGetSortProducts: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myGetSortSpecialist: MutableLiveData<Response<SpecialistIndexModels>> = MutableLiveData()
    val mySpecialistShow: MutableLiveData<Response<SpecialistShowModels>> = MutableLiveData()
    val mySpecialistPage: MutableLiveData<Response<SpecialistIndexModels>> = MutableLiveData()
    val mySimilar: MutableLiveData<Response<SimilarModels>> = MutableLiveData()
    val myShopsModels: MutableLiveData<Response<ShopsModels>> = MutableLiveData()
    val myTovarPage: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myKursy: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val myTovarTradeIn: MutableLiveData<Response<ProductIndexModels>> = MutableLiveData()
    val mySortView: MutableLiveData<Response<RatingAVGModels>> = MutableLiveData()
    val mySortRating: MutableLiveData<Response<RatingAVGModels>> = MutableLiveData()
    val myGetCompare: MutableLiveData<Response<CompareModels>> = MutableLiveData()
    val myGetMessage: MutableLiveData<Response<MessageIndexModels>> = MutableLiveData()
    val myReviews: MutableLiveData<Response<ReviewProductModels>> = MutableLiveData()
    val myReviewsSpecialist: MutableLiveData<Response<ReviewSpecialistModels>> = MutableLiveData()
    val myReviewsPost: MutableLiveData<Response<ReviewPostModels>> = MutableLiveData()
    val myGetCategoryID: MutableLiveData<Response<CategoryShowModels>> = MutableLiveData()
    val myGetCategoryEnd: MutableLiveData<Response<CategoryShowModels>> = MutableLiveData()
    val myCategoryIndex: MutableLiveData<Response<CategoryIndexModels>> = MutableLiveData()
    val mySkillsIndexModels: MutableLiveData<Response<SkillsModels>> = MutableLiveData()
    val mySpecializationIndex: MutableLiveData<Response<SpecializationModels>> = MutableLiveData()
    val myMessageShow: MutableLiveData<Response<MessageShowModels>> = MutableLiveData()
    private val posLikeList: MutableLiveData<Response<LikeProductModels>> = MutableLiveData()
    private val postSpecialistList: MutableLiveData<Response<LikeSpecialistModels>> =
        MutableLiveData()
    private val posRatingList: MutableLiveData<Response<ErrorModels>> = MutableLiveData()
    val myFavoriteList: MutableLiveData<Response<FavoriteModels>> = MutableLiveData()
    val myNotificationList: MutableLiveData<Response<NotificationModels>> = MutableLiveData()
    val myShowNotification: MutableLiveData<Response<NotificationShowModels>> = MutableLiveData()
    val myUser: MutableLiveData<Response<UserModels>> = MutableLiveData()
    private val mySendNotify: MutableLiveData<Response<UserModels>> = MutableLiveData()
    val myCountry: MutableLiveData<Response<CountryIndexModels>> = MutableLiveData()
    val myCity: MutableLiveData<Response<CountryShowModels>> = MutableLiveData()
    val myNewMessageModels: MutableLiveData<Response<NewMessageModels>> = MutableLiveData()
    val mySendEmail: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    val mySendPhone: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    val mySendCheckEmail: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    val myCodeCheckEmail: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    val myResetPassword: MutableLiveData<Response<LoginModels>> = MutableLiveData()
    val mySendCheckPhone: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    val myUpdateCreate: MutableLiveData<Response<Errors>> = MutableLiveData()
    val myUpdateKursy: MutableLiveData<Response<Errors>> = MutableLiveData()
    var mySpecialistCreate: MutableLiveData<Response<SpecialistShowModels>> = MutableLiveData()
    var mySpecialistPut: MutableLiveData<Response<ErrorModels>> = MutableLiveData()
    var myReport: MutableLiveData<Response<ReportModels>> = MutableLiveData()
    var myFargotEmail: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()

    //    var myFastPhone: MutableLiveData<Response<ActivitedSuccessModels>> = MutableLiveData()
    private val myList = MutableLiveData<String>()
    val myupdateUserPost: MutableLiveData<Response<ErrorModels>> = MutableLiveData()


    fun fargotEmail(email: String) {
        viewModelScope.launch {
            myFargotEmail.value = repo.fargotEmailRepository(email)
        }
    }


    val myFastPhone: LiveData<Response<ActivitedSuccessModels>> =
        myList.switchMap {
            repo.fastPhoneRepository(it)
        }

    fun fastPhone(phone: String) {
        viewModelScope.launch {
            myList.postValue(phone)
        }
    }

//    fun fastPhone(phone: String) {
//        viewModelScope.launch {
//            myFastPhone.value = repo.fastPhoneRepository(phone)
//        }
//    }

    fun getState() {
        viewModelScope.launch {
            myStats.value = repo.getStateRepository()
        }
    }

    fun getVersion() {
        viewModelScope.launch {
            myVersion.value = repo.getVersionRepository()
        }
    }

    fun getServices() {
        viewModelScope.launch {
            myServices.value = repo.getServicesRepository()
        }
    }

    fun payGenerate(auth: String, allPro: HashMap<String, String>) {
        viewModelScope.launch {
            myPayGenerate.value = repo.payGenerateRepository(auth, allPro)
        }
    }

    fun payGenerateBanner(
        auth: String,
        allPro: HashMap<String, String>,
        images: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            myPayGenerateBanner.value = repo.payGenerateBannerRepository(auth, allPro, images)
        }
    }

    fun payGenerateStory(
        auth: String,
        allPro: HashMap<String, String>,
        images: List<MultipartBody.Part?>,
        img: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            myPayGenerateStory.value = repo.payGenerateStoryRepository(auth, allPro, images, img)
        }

    }

    fun getStory() {
        viewModelScope.launch {
            myStory.value = repo.getStoryRepository()
        }
    }

    val getBannerPost: LiveData<Response<BannerIndexModels>> =
        myBanner.switchMap {
            repo.getBannerRepository()
        }

    fun setBannerPost() {
        viewModelScope.launch {
            myBanner.postValue(null)
        }
    }


    fun getFavorite(auth: String) {
        viewModelScope.launch {
            myFavoriteList.value = repo.getFavoriteRepository(auth)
        }
    }

    fun getNotification(auth: String) {
        viewModelScope.launch {
            myNotificationList.value = repo.getNotificationRepository(auth)
        }
    }

    fun showNotification(auth: String, idNotification: String) {
        viewModelScope.launch {
            myShowNotification.value = repo.showNotificationRepository(auth, idNotification)
        }
    }

    fun getUser(auth: String) {
        viewModelScope.launch {
            myUser.value = repo.getUserRepository(auth)
        }
    }

    fun sendNotify(auth: String, method: String, tokenNotify: String) {
        viewModelScope.launch {
            mySendNotify.value = repo.sendNotifyRepository(auth, method, tokenNotify)
        }
    }

    fun getCountry() {
        viewModelScope.launch {
            myCountry.value = repo.getCountryRepository()
        }
    }

    fun getCity(number: Int) {
        viewModelScope.launch {
            myCity.value = repo.getCityRepository(number)
        }
    }

    fun getUpdateDataArrayUpdate(auth: String, number: Int) {
        viewModelScope.launch {
            myShowProducts.value = repo.getUpdateDataArrayUpdateRepository(auth, number)
        }
    }

    fun getShopsON(
        auth: String,
        idShop: String
    ) {
        viewModelScope.launch {
            myShopsON.value = repo.getShopsONRepository(auth, idShop)
        }
    }

    fun getShopsOFF(
        auth: String,
        idShop: String
    ) {
        viewModelScope.launch {
            myShopsOFF.value = repo.getShopsOFFRepository(auth, idShop)
        }
    }


    fun postDeleteProduct(
        auth: String,
        delete: String,
        idProduct: Int
    ) {
        viewModelScope.launch {
            myDeleteProduct.value = repo.postDeleteProductRepository(auth, delete, idProduct)
        }
    }

    fun postDisabledProduct(
        auth: String,
        idProduct: Int
    ) {
        viewModelScope.launch {
            myDisabledProduct.value = repo.postDisabledProductRepository(auth, idProduct)
        }
    }


    fun getFilterProducts(auth: String, allPro: HashMap<String, String>) {
        viewModelScope.launch {
            myFilterProducts.value = repo.getFilterProductsRepository(auth, allPro)
        }
    }

    fun getFilterProductsEND(auth: String, allPro: HashMap<String, String>) {
        viewModelScope.launch {
            myFilterProductsEND.value = repo.getFilterProductsRepository(auth, allPro)
        }
    }

    fun getSortProducts(auth: String, params: HashMap<String, String>,filters: HashMap<String, String>, cities: List<Int>) {
        viewModelScope.launch {
            myGetSortProducts.value = repo.getSortProductsRepository(auth, params, filters, cities)
        }
    }

    fun getSortSpecialist(auth: String, params: HashMap<String, String>,filters: HashMap<String, String>, cities: List<Int>) {
        viewModelScope.launch {
            myGetSortSpecialist.value = repo.getSortSpecialistRepository(auth, params, filters, cities)
        }
    }


    fun getSpecialistPage(auth: String) {
        viewModelScope.launch {
            mySpecialistPage.value = repo.getSpecialistPageRepository(auth)
        }
    }

    fun getSpecialistShow(auth: String, number: Int) {
        viewModelScope.launch {
            mySpecialistShow.value = repo.getSpecialistShowRepository(auth, number)
        }
    }

    fun getSimilar(auth: String, number: Int) {
        viewModelScope.launch {
            mySimilar.value = repo.getSimilarRepository(auth, number)
        }
    }

    fun getShops(number: Int) {
        viewModelScope.launch {
            myShopsModels.value = repo.getShopsRepository(number)
        }
    }

    fun postDeleteShop(
        auth: String,
        delete: String,
        idProduct: Int
    ) {
        viewModelScope.launch {
            myDeleteShop.value = repo.postDeleteShopRepository(auth, delete, idProduct)
        }
    }


    fun getSortView(auth: String, sort: String) {
        viewModelScope.launch {
            mySortView.value = repo.getSortViewRepository(auth, sort)
        }
    }

    fun getTovarPage(auth: String) {
        viewModelScope.launch {
            myTovarPage.value = repo.getTovarPageRepository(auth)
        }
    }

    fun getKursy(auth: String) {
        viewModelScope.launch {
            myKursy.value = repo.getKursyRepository(auth)
        }
    }

    fun getTovarTradeIn(auth: String) {
        viewModelScope.launch {
            myTovarTradeIn.value = repo.getTovarTradeInRepository(auth)
        }
    }

    fun getSortRating(auth: String, sort: String) {
        viewModelScope.launch {
            mySortRating.value = repo.getSortRatingRepository(auth, sort)
        }
    }


    fun getCategoryIndex(auth: String) {
        viewModelScope.launch {
            myCategoryIndex.value = repo.getCategoryIndexRepository(auth)
        }
    }

    fun getSkillsIndex(auth: String) {
        viewModelScope.launch {
            mySkillsIndexModels.value = repo.getSkillsIndexRepository(auth)
        }
    }

    fun getSpecializationIndex(auth: String) {
        viewModelScope.launch {
            mySpecializationIndex.value = repo.getSpecializationIndexRepository(auth)
        }
    }

    fun getMessageIndex(auth: String) {
        viewModelScope.launch {
            myGetMessage.value = repo.getMessageIndexRepository(auth)
        }
    }

    fun getReviews(number: Int) {
        viewModelScope.launch {
            myReviews.value = repo.getReviewsRepository(number)
        }
    }

    fun getReviewsSpecialist(number: Int) {
        viewModelScope.launch {
            myReviewsSpecialist.value = repo.getReviewsSpecialistRepository(number)
        }
    }

    fun postReview(auth: String, text: String, type: String, review_id: Int) {
        viewModelScope.launch {
            myReviewsPost.value = repo.postReviewRepository(auth, text, type, review_id)
        }

//        Util.uLogD("Review = " +
//                "\n" +
//                "\nauth - $auth" +
//                "\ntext - $text" +
//                "\ntype - $type" +
//                "\nreview_id - $review_id")
    }

    fun getCategoryID(auth: String, number: Int) {
        viewModelScope.launch {
            myGetCategoryID.value = repo.getCategoryIDRepository(auth, number)
        }
    }

    fun getCategoryIDEnd(auth: String, number: Int) {
        viewModelScope.launch {
            myGetCategoryEnd.value = repo.getCategoryIDRepository(auth, number)
        }
    }

    fun getCompareID(auth: String, number: Int, id: String) {
        viewModelScope.launch {
            myGetCompare.value = repo.getCompareIDRepository(auth, number, id)
        }
    }

    fun postLike(auth: String, number: Int) {
        viewModelScope.launch {
            posLikeList.value = repo.postLikeRepository(auth, number)
        }
    }

    fun getMessageShow(auth: String, number: Int) {
        viewModelScope.launch {
            myMessageShow.value = repo.getMessageShowRepository(auth, number)
        }
    }

    fun postSpecialist(auth: String, number: Int) {
        viewModelScope.launch {
            postSpecialistList.value = repo.postSpecialistRepository(auth, number)
        }
    }

    fun postRating(auth: String, name: String, idProduct: Int, rating: String) {
        viewModelScope.launch {
            posRatingList.value = repo.postRatingRepository(auth, name, idProduct, rating)
        }
    }

    fun newMessage(auth: String, params: HashMap<String, String>) {
        viewModelScope.launch {
            myNewMessageModels.value = repo.newMessage(auth, params)
        }
    }

    fun sendEmail(auth: String) {
        viewModelScope.launch {
            mySendEmail.value = repo.sendEmailRepository(auth)
        }
    }

    fun sendPhone(auth: String) {
        viewModelScope.launch {
            mySendPhone.value = repo.sendPhoneRepository(auth)
        }
    }

    fun sendCheckEmail(auth: String, code: String) {
        viewModelScope.launch {
            mySendCheckEmail.value = repo.sendCheckEmailRepository(auth, code)
        }
    }

    fun codeCheckEmail(auth: String, code: String) {
        viewModelScope.launch {
            myCodeCheckEmail.value = repo.codeCheckEmailRepository(auth, code)
        }
    }

    fun resetPassword(code: String, password: String, password2: String) {
        viewModelScope.launch {
            myResetPassword.value = repo.resetPasswordRepository(code, password, password2)
        }
    }

    fun sendCheckPhone(auth: String, code: String) {
        viewModelScope.launch {
            mySendCheckPhone.value = repo.sendCheckPhoneRepository(auth, code)
        }
    }

    fun pushSpecialistCreate(
        auth: String,
        params: HashMap<String, RequestBody>,
        params2: HashMap<String, RequestBody>,
        params3: HashMap<String, RequestBody>,
        params4: HashMap<String, RequestBody>,
        img: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            mySpecialistCreate.value = repo.pushSpecialistCreateRepository(
                auth,
                params,
                params2,
                params3,
                params4,
                img
            )
        }


    }

    fun pushSpecialistPut(
        auth: String,
        number: Int,
        params: HashMap<String, RequestBody>,
        params2: HashMap<String, RequestBody>,
        params3: HashMap<String, RequestBody>,
        params4: HashMap<String, RequestBody>,
        img: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            mySpecialistPut.value = repo.pushSpecialistPutRepository(
                auth,
                number,
                params,
                params2,
                params3,
                params4,
                img
            )
        }
    }

    fun postReport(
        auth: String,
        id: Int,
        type: String,
        desc: String,
        view: String
    ) {
        viewModelScope.launch {
            myReport.value = repo.postReportRepository(
                auth,
                id,
                type,
                desc,
                view
            )
        }
    }


    fun updateUserPost(
        auth: String,
        params: HashMap<String, RequestBody>,
        img: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            myupdateUserPost.value = repo.updateUserPostRepository(
                auth,
                params,
                img
            )
        }
    }

    fun pushUpdateCreate(
        auth: String,
        number: Int,
        _method: RequestBody,
        name: RequestBody,
        country_id: RequestBody,
        category_id: RequestBody,
        brand_id: RequestBody,
        state: RequestBody,
        price: RequestBody,
        img: MultipartBody.Part?,
        description: RequestBody,
        params: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part>?,
        installment: HashMap<String, RequestBody>,
    ) {
        viewModelScope.launch {
            myUpdateCreate.value = repo.pushUpdateCreate(
                auth,
                number,
                _method,
                name,
                country_id,
                category_id,
                brand_id,
                state,
                price,
                img,
                description,
                params,
                images,
                installment
            )
        }
    }

    fun pushUpdateKursy(
        auth: String,
        number: Int,
        _method: RequestBody,
        type: RequestBody,
        name: RequestBody,
        country_id: RequestBody,
        price: RequestBody,
        img: MultipartBody.Part?,
        description: RequestBody,
        images: List<MultipartBody.Part>?,
        installment: HashMap<String, RequestBody>?
    ) {
        viewModelScope.launch {
            myUpdateKursy.value = repo.pushUpdateKursyRepository(
                auth,
                number,
                _method,
                type,
                name,
                country_id,
                price,
                img,
                description,
                images,
                installment
            )
        }
    }
}