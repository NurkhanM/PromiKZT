package product.promikz

import android.widget.TextView
import okhttp3.RequestBody


object AppConstants {

    const val VERSION_APK = "0.0.6"


    const val APP_PREFERENCES = "APP_PREFERENCES"
    const val KEY_INFO = "KEY_INFO"
    const val KEY_ID_SHOP_MY = "KEY_ID_SHOP_MY"
    const val KEY_THEME = "KEY_THEME"
    const val KEY_LANGUAGE = "KEY_LANGUAGE"
    const val KEY_THEME_SELECTED = "KEY_THEME_SELECTED"
    const val KEY_TOKEN = "KEY_PASSWORD"

    const val INTENT_FILTER = "PUSH_EVENT"
    const val KEY_ACTION = "action"
    const val KEY_MESSAGE = "message"
    const val ACION_SHOW_MESSAGE = "show_message"

    var TOKEN_USER = "null"
    var ID_SHOP_USER = -1
    var USER_ID: Int? = -1
    var USER_OTHER_ID = -1
    var ID_SHOP_MY = -1
    var ID_PAY = -1
    var USER_TYPE = ""
    var FILTERS_TYPE = ""
    var VERIFY_USER_EMAIL = false
    var VERIFY_USER_PHONE = false

    var userImageActivity = "https://vsekidki.ru/uploads/posts/2016-08/1470735121_lecdaa3axdc.jpg"
    var PRIVACY_USER = "https://promi.kz/privacyPolicy"
    var PRIVACY_AGREEMENT = "https://promi.kz/userAgreement"



    var shopInfo = false
    var categoryState = false
    var getCategoryID = -1
    var getProductID = -1
    var MY_SPECIALIST = -1
    var SPECIALIST_ALL = -1
    var getSpecialistIDSTATE = false
    var getCategorySearchSortID = -1
    var getCategoryFiltersSortID = -1
    var getCategoryCompareID = -2
    var getBrandID = -1
    var userIDChat: Int? = -1

//    var possitionHome: Int? = 0

    var compareAll = ArrayList<Int>()

    var specialistAllNumber = ArrayList<Int>()
    var specialistAllNumber2 = ArrayList<Int>()
    var SEARCH_PARAMS_NUMBER = ArrayList<Int>()
    val SEARCH_TEXTVIEW_TITLE = ArrayList<TextView>()

    var ARRAY_LIST_FILTER = ArrayList<ArrayList<String>>()
    var ARRAY_LIST_FILTER_ID =  ArrayList<Int>()
    var ARRAY_LIST_FILTER_INDEX = 0

    var specializationAllNumber = ArrayList<Int>()
    var specializationAllNumber2 = ArrayList<Int>()

    var imagesStoryAll = ArrayList<String>()
    var imagesStoryFollow = ArrayList<String?>()

    var params = HashMap<String, RequestBody>()
    var params2 =  HashMap<String, RequestBody>()
    var params3 =  HashMap<String, RequestBody>()
    var params4 =  HashMap<String, RequestBody>()



    var followBannerString = ArrayList<String>()
    var PROGRESS_COUNT = 1

    var isStatusServer: Boolean = true
    var isStatusMultiImages: Boolean = false

    var STATE_INT_FILTERS = true
    val MAP_FILTERS_PRODUCTS = HashMap<String, String>()
    var CATEGORY_INT_FILTERS_DATA = 0
    var CATEGORY_INT_SEARCH_DATA = 0
    var BRAND_INT_FILTERS_DATA = 0
    var COUNTRY_ID: Int? = 0
    var COUNTRY_ID_ARRAY = ArrayList<Int>()
    var CHECK_CODE_RESET = "0"
    var FAST_PHONE = ""



    var totalCountProduct = 0
    var totalCountSchool = 0
    var totalCountSpecialist = 0
    var totalNotification = 0
    var priceMAX = 0
    var priceAVG = 0
    var priceMIN = 0

    var userTelephony = "+996500051001"

    //search
    var MAP_SEARCH = HashMap<String, String>()
    var MAP_SEARCH_CITIES: List<Int> = emptyList<Int>()
    var MAP_SEARCH_DOP_FILTERS = HashMap<String, String>()


    var FILTER_INT_ALL = ArrayList<Int>()

}