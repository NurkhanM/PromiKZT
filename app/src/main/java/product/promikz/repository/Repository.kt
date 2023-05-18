package product.promikz.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import product.promikz.data.RetroFitInstance
import product.promikz.models.story.index.StoryIndex
import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.auth.email.one.ActivitedSuccessModels
import product.promikz.models.auth.login.LoginModels
import product.promikz.models.auth.register.RegisterModels
import product.promikz.models.banner.BannerIndexModels
import product.promikz.models.errors.ErrorModels
import product.promikz.models.specialization.SpecializationModels
import product.promikz.models.version.VersionModels
import product.promikz.models.like.product.LikeProductModels
import product.promikz.models.pay.generete.PayGenereteModels
import product.promikz.models.pay.index.PayModels
import product.promikz.models.post.report.ReportModels
import product.promikz.models.stats.StateModels
import product.promikz.models.category.index.CategoryIndexModels
import product.promikz.models.category.show.CategoryShowModels
import product.promikz.models.compare.CompareModels
import product.promikz.models.country.index.CountryIndexModels
import product.promikz.models.country.show.CountryShowModels
import product.promikz.models.errors.Errors
import product.promikz.models.favorite.FavoriteModels
import product.promikz.models.like.specialist.LikeSpecialistModels
import product.promikz.models.message.index.MessageIndexModels
import product.promikz.models.message.show.MessageShowModels
import product.promikz.models.notification.index.NotificationModels
import product.promikz.models.notification.show.NotificationShowModels
import product.promikz.models.post.message.store.NewMessageModels
import product.promikz.models.post.shopCreate.ShopCreateM
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
import product.promikz.models.story.show.StoryShowModels
import product.promikz.models.subscriber.category.SubCategoryModels
import product.promikz.models.subscriber.index.IndexSubscriberModels
import product.promikz.models.subscriber.shop.SubShopModels
import product.promikz.models.user.UserModels
import retrofit2.Response

class Repository {

    suspend fun fargotEmailRepository(email: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.fargotEmail(email)
    }

//    suspend fun fastPhoneRepository(phone: String): Response<ActivitedSuccessModels> {
//        return RetroFitInstance.authAPI.fastPhone(phone)
//    }

    fun fastPhoneRepository(phone: String): LiveData<Response<ActivitedSuccessModels>> =
        liveData {
            val response = RetroFitInstance.authAPI.fastPhone(phone)
            emit(response)
        }

    fun pushPostRepository(params: HashMap<String, String>): LiveData<Response<LoginModels>> =
        liveData {
            val response = RetroFitInstance.authAPI.pushPost(params)
            emit(response)
        }

    fun getBannerRepository(): LiveData<Response<BannerIndexModels>> =
        liveData {
            val response = RetroFitInstance.api.getBanner()
            emit(response)
        }

    suspend fun getStateRepository(): Response<StateModels> {
        return RetroFitInstance.api.getState()
    }

    suspend fun getVersionRepository(): Response<VersionModels> {
        return RetroFitInstance.settingsApi.getVersion()
    }

    suspend fun getServicesRepository(): Response<PayModels> {
        return RetroFitInstance.payApi.getServices()
    }

    suspend fun payGenerateRepository(auth: String, allPro: HashMap<String, String>): Response<PayGenereteModels> {
        return RetroFitInstance.payApi.payGenerate(auth, allPro)
    }

    suspend fun payGenerateBannerRepository(auth: String, allPro: HashMap<String, String>, images: MultipartBody.Part?): Response<PayGenereteModels> {
        return RetroFitInstance.payApi.payGenerateBanner(auth, allPro, images)
    }
    suspend fun payGenerateStoryRepository(auth: String, allPro: HashMap<String, String>, images: List<MultipartBody.Part?>, img: MultipartBody.Part?): Response<PayGenereteModels> {
        return RetroFitInstance.payApi.payGenerateStory(auth, allPro, images, img)
    }


    suspend fun getCountryRepository(): Response<CountryIndexModels> {
        return RetroFitInstance.countryApi.getCountry()
    }

    suspend fun getCityRepository(number: Int): Response<CountryShowModels> {
        return RetroFitInstance.countryApi.getCity(number)
    }

    suspend fun getStoryRepository(): Response<StoryIndex> {
        return RetroFitInstance.storyApi.getStory()
    }

    suspend fun getStoryShowRepository(idStory: String): Response<StoryShowModels> {
        return RetroFitInstance.storyApi.getStoryShow(idStory)
    }


    suspend fun getFavoriteRepository(auth: String): Response<FavoriteModels> {
        return RetroFitInstance.userApi.getFavorite(auth)
    }

    suspend fun getNotificationRepository(auth: String): Response<NotificationModels> {
        return RetroFitInstance.notificationApi.getNotification(auth)
    }

    suspend fun showNotificationRepository(auth: String, idNotification: String): Response<NotificationShowModels> {
        return RetroFitInstance.notificationApi.showNotification(auth, idNotification)
    }

    suspend fun getUserRepository(auth: String): Response<UserModels> {
        return RetroFitInstance.userApi.getUser(auth)
    }

    suspend fun sendNotifyRepository(auth: String, method: String, tokenNotify: String): Response<UserModels> {
        return RetroFitInstance.userApi.sendNotify(auth, method, tokenNotify)
    }

    suspend fun getUpdateDataArrayUpdateRepository(
        auth: String,
        number: Int,
    ): Response<ProductShowModels> {
        return RetroFitInstance.productApi.getUpdateDataArrayUpdate(auth, number)
    }

    suspend fun subCategoryRepository(
        auth: String,
        number: Int,
    ): Response<SubCategoryModels> {
        return RetroFitInstance.subscriberApi.subCategory(auth, number)
    }

    suspend fun subIndexRepository(
        auth: String
    ): Response<IndexSubscriberModels> {
        return RetroFitInstance.subscriberApi.subIndex(auth)
    }

    suspend fun subShopRepository(
        auth: String,
        number: Int,
    ): Response<SubShopModels> {
        return RetroFitInstance.subscriberApi.subShop(auth, number)
    }

    suspend fun getShopsONRepository(
        auth: String,
        idShop: String
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getShopsON(auth, idShop)
    }

    suspend fun getShopsOFFRepository(
        auth: String,
        idShop: String
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getShopsOFF(auth, idShop)
    }

    suspend fun postDeleteProductRepository(
        auth: String,
        delete: String,
        idProduct: Int
    ): Response<String> {
        return RetroFitInstance.productApi.postDeleteProduct(auth,delete, idProduct )
    }

    suspend fun postDisabledProductRepository(
        auth: String,
        idProduct: Int
    ): Response<String> {
        return RetroFitInstance.productApi.postDisabledProduct(auth, idProduct )
    }

    suspend fun getFilterProductsRepository(
        auth: String,
        allPro: HashMap<String, String>
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getFilterProducts(auth, allPro)
    }

    suspend fun getSortProductsRepository(
        auth: String,
        params: HashMap<String, String>,
        filters: HashMap<String, String>,
        cities: List<Int>
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getSortProducts(auth, params,filters, cities)
    }

    suspend fun getSortSpecialistRepository(
        auth: String,
        params: HashMap<String, String>,
        filters: HashMap<String, String>,
        cities: List<Int>
    ): Response<SpecialistIndexModels> {
        return RetroFitInstance.productApi.getSortSpecialist(auth, params,filters, cities)
    }

    suspend fun getSpecialistPageRepository(
        auth: String,
    ): Response<SpecialistIndexModels> {
        return RetroFitInstance.specialistApi.getSpecialistPage(auth)
    }

    suspend fun getSpecialistShowRepository(
        auth: String,
        number: Int,
    ): Response<SpecialistShowModels> {
        return RetroFitInstance.specialistApi.getSpecialistShow(auth, number)
    }

    suspend fun getSimilarRepository(
        auth: String,
        number: Int,
    ): Response<SimilarModels> {
        return RetroFitInstance.api.getSimilar(auth, number)
    }

    suspend fun getShopsRepository(
        auth: String,
        number: Int,
    ): Response<ShopsModels> {
        return RetroFitInstance.shopApi.getShops(auth, number)
    }

    suspend fun postDeleteShopRepository(
        auth: String,
        delete: String,
        idProduct: Int
    ): Response<String> {
        return RetroFitInstance.shopApi.postDeleteShop(auth,delete, idProduct )
    }

    suspend fun getSortViewRepository(
        auth: String,
        sort: String,
    ): Response<RatingAVGModels> {
        return RetroFitInstance.productApi.getSortViews(auth, sort)
    }

    suspend fun getTovarPageRepository(
        auth: String
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getTovarPage(auth)
    }

    suspend fun getKursyRepository(
        auth: String
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getKursy(auth)
    }

    suspend fun getTovarTradeInRepository(
        auth: String
    ): Response<ProductIndexModels> {
        return RetroFitInstance.productApi.getTovarTradeIn(auth)
    }

    suspend fun getSortRatingRepository(
        auth: String,
        sort: String,
    ): Response<RatingAVGModels> {
        return RetroFitInstance.productApi.getSortRating(auth, sort)
    }


    suspend fun getCompareIDRepository(
        auth: String,
        number: Int,
        id: String,
    ): Response<CompareModels> {
        return RetroFitInstance.compareApi.getCompareID(auth, number, id)
    }

    suspend fun getCategoryIndexRepository(
        auth: String,
    ): Response<CategoryIndexModels> {
        return RetroFitInstance.categoryApi.getCategoryIndex(auth)
    }

    suspend fun getSkillsIndexRepository(
        auth: String,
    ): Response<SkillsModels> {
        return RetroFitInstance.skillApi.getSkillsIndex(auth)
    }

    suspend fun getSpecializationIndexRepository(
        auth: String,
    ): Response<SpecializationModels> {
        return RetroFitInstance.specializationApi.getSpecializationIndex(auth)
    }

    suspend fun getMessageIndexRepository(
        auth: String,
    ): Response<MessageIndexModels> {
        return RetroFitInstance.messageApi.getMessageIndex(auth)
    }

    suspend fun getCategoryIDRepository(
        auth: String,
        number: Int,
    ): Response<CategoryShowModels> {
        return RetroFitInstance.categoryApi.getCategoryID(auth, number)
    }

    suspend fun getReviewsRepository(
        number: Int,
    ): Response<ReviewProductModels> {
        return RetroFitInstance.reviewApi.getReviews(number)
    }

    suspend fun getReviewsSpecialistRepository(
        number: Int,
    ): Response<ReviewSpecialistModels> {
        return RetroFitInstance.reviewApi.getReviewsSpecialist(number)
    }

    suspend fun postReviewRepository(
        auth: String, text: String, type: String, review_id: Int
    ): Response<ReviewPostModels> {
        return RetroFitInstance.reviewApi.postReview(auth, text, type, review_id)
    }

    suspend fun getMessageShowRepository(
        auth: String,
        number: Int,
    ): Response<MessageShowModels> {
        return RetroFitInstance.messageApi.getMessageShow(auth, number)
    }

    suspend fun postLikeRepository(auth: String, number: Int): Response<LikeProductModels> {
        return RetroFitInstance.api.postLike(auth, number)
    }

    suspend fun postSpecialistRepository(auth: String, number: Int): Response<LikeSpecialistModels> {
        return RetroFitInstance.api.postSpecialist(auth, number)
    }

    suspend fun postRatingRepository(
        auth: String,
        name: String,
        idProduct: Int,
        rating: String,
    ): Response<ErrorModels> {
        return RetroFitInstance.api.postRating(auth, name, idProduct, rating)
    }


    suspend fun newMessage(auth: String, params: HashMap<String, String>): Response<NewMessageModels> {
        return RetroFitInstance.messageApi.newMessage(auth, params)
    }

    suspend fun sendEmailRepository(auth: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.sendEmail(auth)
    }

    suspend fun sendPhoneRepository(auth: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.sendPhone(auth)
    }

    suspend fun sendCheckEmailRepository(auth: String, code: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.sendCheckEmail(auth, code)
    }

    suspend fun codeCheckEmailRepository(auth: String, code: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.codeCheckEmail(auth, code)
    }

    suspend fun resetPasswordRepository(code: String, password: String, password2: String): Response<LoginModels> {
        return RetroFitInstance.authAPI.resetPassword(code, password, password2)
    }

    suspend fun sendCheckPhoneRepository(auth: String, code: String): Response<ActivitedSuccessModels> {
        return RetroFitInstance.authAPI.sendCheckPhone(auth, code)
    }


    suspend fun pushRegistPost(
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
        password_confirmation: RequestBody,
        country_id: RequestBody,
        type: RequestBody,
        phone: RequestBody,
        img: MultipartBody.Part?,
    ): Response<RegisterModels> {
        return RetroFitInstance.authAPI.pushRegistPost(
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

    suspend fun fastRegisterRepository(
        phone: RequestBody,
        typeRegister: RequestBody,
    ): Response<RegisterModels> {
        return RetroFitInstance.authAPI.fastRegister(
            phone,
            typeRegister,
        )
    }

    suspend fun updateUserPostRepository(
        auth: String,
        params: HashMap<String, RequestBody>,
        img: MultipartBody.Part?
    ): Response<ErrorModels> {
        return RetroFitInstance.userApi.updateUserPost(
            auth,
            params,
            img
        )
    }

    suspend fun pushShopCreate(
        auth: String,
        name: RequestBody,
        description: RequestBody,
        img: MultipartBody.Part?,
    ): Response<ShopCreateM> {
        return RetroFitInstance.shopApi.pushShopCreate(
            auth,
            name,
            description,
            img
        )
    }

    suspend fun updateShopCreateRepository(
        auth: String,
        method: RequestBody,
        idShop: RequestBody,
        name: RequestBody,
        description: RequestBody,
        img: MultipartBody.Part?
    ): Response<ShopCreateM> {
        return RetroFitInstance.shopApi.updateShopCreate(
            auth,
            method,
            idShop,
            name,
            description,
            img
        )
    }

    suspend fun pushProductCreate(
        auth: String,
        params: HashMap<String, RequestBody>,
        fields: HashMap<String, RequestBody>,
        img: MultipartBody.Part?,
        images: List<MultipartBody.Part>,

        ): Response<ErrorModels> {
        return RetroFitInstance.productApi.pushProductCreate(
            auth,
            params,
            fields,
            img,
            images
        )
    }
    suspend fun pushSpecialistCreateRepository(
        auth: String,
        params: HashMap<String, RequestBody>,
        params2:   HashMap<String, RequestBody>,
        params3:   HashMap<String, RequestBody>,
        params4: HashMap<String, RequestBody>,
        img: MultipartBody.Part?

        ): Response<SpecialistShowModels> {
        return RetroFitInstance.specialistApi.pushSpecialistCreate(
            auth,
            params,
            params2,
            params3,
            params4,
            img
        )
    }

    suspend fun pushSpecialistPutRepository(
        auth: String,
        number: Int,
        params: HashMap<String, RequestBody>,
        params2:   HashMap<String, RequestBody>,
        params3:   HashMap<String, RequestBody>,
        params4:   HashMap<String, RequestBody>,
        img: MultipartBody.Part?

        ): Response<ErrorModels> {
        return RetroFitInstance.specialistApi.pushSpecialistPut(
            auth,
            number,
            params,
            params2,
            params3,
            params4,
            img
        )
    }

    suspend fun postReportRepository(
        auth: String,
        id: Int,
        type: String,
        desc: String,
        view: String

        ): Response<ReportModels> {
        return RetroFitInstance.reportApi.postReport(
            auth,
            id,
            type,
            desc,
            view
        )
    }

    suspend fun pushUpdateCreate(
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
        params: HashMap<String, RequestBody>?,
        images: List<MultipartBody.Part>?,
        installment: HashMap<String, RequestBody>?,

        ): Response<Errors> {
        return RetroFitInstance.productApi.pushUpdateCreate(
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

    suspend fun pushUpdateKursyRepository(
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

        ): Response<Errors> {
        return RetroFitInstance.productApi.pushUpdateKursy(
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