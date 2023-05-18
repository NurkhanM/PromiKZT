package product.promikz.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import product.promikz.data.api.ApiService
import product.promikz.data.auth.AuthAPI
import product.promikz.data.category.CategoryApi
import product.promikz.data.compare.CompareApi
import product.promikz.data.country.CountryApi
import product.promikz.data.message.MessageApi
import product.promikz.data.notifications.NotificationApi
import product.promikz.data.pay.PayApi
import product.promikz.data.products.ProductApi
import product.promikz.data.report.ReportApi
import product.promikz.data.reviews.ReviewApi
import product.promikz.data.settings.SettingsApi
import product.promikz.data.shop.ShopApi
import product.promikz.data.skills.SkillApi
import product.promikz.data.specialist.SpecialistApi
import product.promikz.data.specialization.SpecializationApi
import product.promikz.data.story.StoryApi
import product.promikz.data.subscriber.SubscriberApi
import product.promikz.data.user.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetroFitInstance {
    private val retrofit by lazy {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.MINUTES) // write timeout
            .readTimeout(30, TimeUnit.MINUTES) // read timeout
            .addInterceptor(interceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://promi.kz")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }


    val authAPI: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val shopApi: ShopApi by lazy {
        retrofit.create(ShopApi::class.java)
    }
    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
    val categoryApi: CategoryApi by lazy {
        retrofit.create(CategoryApi::class.java)
    }
    val compareApi: CompareApi by lazy {
        retrofit.create(CompareApi::class.java)
    }
    val countryApi: CountryApi by lazy {
        retrofit.create(CountryApi::class.java)
    }
    val messageApi: MessageApi by lazy {
        retrofit.create(MessageApi::class.java)
    }
    val notificationApi: NotificationApi by lazy {
        retrofit.create(NotificationApi::class.java)
    }
    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }
    val reviewApi: ReviewApi by lazy {
        retrofit.create(ReviewApi::class.java)
    }
    val skillApi: SkillApi by lazy {
        retrofit.create(SkillApi::class.java)
    }
    val specialistApi: SpecialistApi by lazy {
        retrofit.create(SpecialistApi::class.java)
    }
    val storyApi: StoryApi by lazy {
        retrofit.create(StoryApi::class.java)
    }
    val payApi: PayApi by lazy {
        retrofit.create(PayApi::class.java)
    }
    val specializationApi: SpecializationApi by lazy {
        retrofit.create(SpecializationApi::class.java)
    }
    val settingsApi: SettingsApi by lazy {
        retrofit.create(SettingsApi::class.java)
    }
    val reportApi: ReportApi by lazy {
        retrofit.create(ReportApi::class.java)
    }
    val subscriberApi: SubscriberApi by lazy {
        retrofit.create(SubscriberApi::class.java)
    }

}