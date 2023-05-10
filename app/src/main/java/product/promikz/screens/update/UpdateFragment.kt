package product.promikz.screens.update

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import org.json.JSONObject
import org.jsoup.Jsoup
import product.promikz.AppConstants
import product.promikz.AppConstants.ID_PAY
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.USER_OTHER_ID
import product.promikz.AppConstants.USER_TYPE
import product.promikz.AppConstants.compareAll
import product.promikz.AppConstants.getCategoryCompareID
import product.promikz.AppConstants.getProductID
import product.promikz.AppConstants.userTelephony
import product.promikz.MyUtils
import product.promikz.MyUtils.uLogD
import product.promikz.R
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentUpdateBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.inteface.IClickListnearUpdateImage
import product.promikz.models.my.string.AutoMessageModels
import product.promikz.models.products.show.Image
import product.promikz.screens.complaint.product.ComplaintProductActivity
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.home.TovarAdapter
import product.promikz.screens.refresh.RefreshActivity
import product.promikz.screens.shop.ReviewAllFragment
import product.promikz.screens.sravnit.CompareActivity
import product.promikz.screens.update.autoMessage.AutoMessageAdapter
import product.promikz.screens.update.showImage.ShowImageActivity
import product.promikz.screens.update.userOther.UserOtherActivity
import java.text.SimpleDateFormat
import java.util.*


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val ed = ArrayList<View>()

    private lateinit var viewModels: HomeViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: UpdateDataAdapter

    private lateinit var recyclerViewReview: RecyclerView
    private lateinit var adapterReview: TovarAdapter

    lateinit var dialog: Dialog
    private lateinit var dialogPay: Dialog
    private lateinit var dialogLoader: Dialog

    private var nameProducts: String = ""
    private var descProduct: String = ""
    private val url: String = "https://promi.kz/product/"
    private var urlProducts: String = ""

    private var isLike: Boolean = false
    private var isReview: Boolean = false
    private var isMoreState: Boolean = true
    private var isHarakter: Boolean = true
    private var isMenu: Boolean = true
    private var idProducts: Int = -1
    private var nameOtherUser: String = ""
    private var phoneOtherUser: String = ""
    private var imageOtherUser: String = ""
    private var descripOtherUser: String = ""
    private var idCategory: Int = -1
    private var idProductsShow: Int = -2
    private var indexShow: Int = 0



    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_rotate_close
        )
    }
    private val rotateFrom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_from_bottom
        )
    }
    private val rotateTo: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_to_bottom
        )
    }
    private var clicked = false
    private val map: HashMap<String, String> = hashMapOf()

    private lateinit var recyclerViewNewMessage: RecyclerView
    private lateinit var adapterAutoMes: AutoMessageAdapter
    private var autoMessageArray = ArrayList<AutoMessageModels>()
    private val newMessageMap = HashMap<String, String>()

    @SuppressLint("CommitTransaction")
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        val view = binding
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]

        val arguments = activity?.intent?.extras
        idProducts = arguments?.getInt("hello") ?: 0
        getProductID = arguments?.getInt("hello") ?: 0
        idProductsShow = arguments?.getInt("hello") ?: 0

        dialog = Dialog(requireContext())
        dialogPay = Dialog(requireContext())
        dialogLoader = Dialog(requireContext())


        // Здесь проверка на Лайк
        view.itemFavorite.setOnClickListener {
            isLike = if (isLike) {
                view.updateImageFavorite.setImageResource(R.drawable.ic_favorite)
                false
            } else {
                Toast.makeText(
                    requireContext(),
                    "${resources.getText(R.string.you_added)} $nameProducts ${
                        resources.getText(
                            R.string.fovarite_to
                        )
                    }",
                    Toast.LENGTH_SHORT
                ).show()
                view.updateImageFavorite.setImageResource(R.drawable.ic_favorite2)
                true
            }

            viewModels.postLike("Bearer $TOKEN_USER", idProducts)
        }

        recyclerView = view.recyclerUpdate
        adapter = UpdateDataAdapter(object : IClickListnearUpdateImage {
            override fun clickListener(baseID: String, index: Int) {
                MyUtils.uGlide(requireContext(), view.imgAddFirst, baseID)
                indexShow = index
            }

        })
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        recyclerViewReview = view.similarUpdateRecyclerView
        recyclerViewReview.addItemDecoration(SpaceItemDecoration(30, true))
        adapterReview = TovarAdapter(object : IClickListnearHomeFavorite {

            override fun clickListener(
                baseID: Int,
                viewAdapter: ItemMakeupModelsBinding,
                isLike: Boolean,
                position: Int
            ) {

                if (!isLike) {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                } else {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                }

                viewModels.postLike("Bearer $TOKEN_USER", baseID)
            }

            // это похожие товары один тотже код
            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {
                if (adver != 3) {
                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
                    intent.putExtra("hello", baseID)
                    startActivity(intent)
                    (activity as AppCompatActivity).overridePendingTransition(
                        R.anim.zoom_enter,
                        R.anim.zoom_exit
                    )
                } else {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
                        startActivity(browserIntent)
                    }catch (e: ActivityNotFoundException){
                        uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

        })
        recyclerViewReview.adapter = adapterReview
        recyclerViewReview.setHasFixedSize(true)

        refReview()

        view.fabId.setOnClickListener {
            onAddButtonClicked()
        }

        view.floatingActionButtonShare.setOnClickListener {
            showSharingDialogAsKotlinWithURL(nameProducts,descProduct, urlProducts)
        }



        view.updateImageCompare.setOnClickListener {
            val intent = Intent(requireActivity(), CompareActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }
        view.floatingActionButtonCompare.setOnClickListener {

            if (getCategoryCompareID != -2) {
                if (getCategoryCompareID == idCategory) {
                    if (isContains(compareAll, idProducts)) {
                        compareAll = removeItem(compareAll, idProducts)
                        if (compareAll.isEmpty()) {
                            getCategoryCompareID = -2
                        }
                    } else {
                        if (compareAll.size > 3) {
                            Toast.makeText(
                                requireContext(),
                                resources.getText(R.string.maximum_4),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            compareAll.add(idProducts)
                            Toast.makeText(
                                requireContext(),
                                "${resources.getText(R.string.you_added)} $nameProducts ${
                                    resources.getText(
                                        R.string.comparing_to
                                    )
                                }",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    isStateSizeShow()

                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.same_category),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                getCategoryCompareID = idCategory
                compareAll.add(idProducts)
                Toast.makeText(
                    requireContext(),
                    "${resources.getText(R.string.you_added)} $nameProducts ${resources.getText(R.string.comparing_to)}",
                    Toast.LENGTH_SHORT
                ).show()
            }


            isStateSizeShow()
        }

        view.floatingActionButtonReview.setOnClickListener {
            if (isReview) {
                val fragment = ReviewAllFragment()
                fragment.setTargetFragment(this, 50)
                parentFragmentManager.beginTransaction()
                    .add(fragment, fragment.tag)
                    .commit()

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.review_disabled),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        view.imgAddFirst.setOnClickListener {
            val intent = Intent(requireActivity(), ShowImageActivity::class.java)
            intent.putExtra("show", idProductsShow)
            intent.putExtra("index", indexShow)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }

        view.clickUpdateBackCard.setOnClickListener {
           activity?.onBackPressed()

        }


        view.fragmentContainerUpdate.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 650) {
                val typedValue = TypedValue()
                val theme: Resources.Theme = requireActivity().theme
                theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
                val primaryColor = typedValue.data
                view.updateToolbar.setBackgroundColor(primaryColor)
            } else {
                view.updateToolbar.setBackgroundColor(0)
            }
        })

        view.updateCallBtn.setOnClickListener {
            callPhone()
        }

        view.btnSendRating.setOnClickListener {
            if (TOKEN_USER != "null") {
                val rating = view.isRatingUser.rating
                viewModels.postRating(
                    "Bearer $TOKEN_USER",
                    "product",
                    idProducts,
                    rating.toString()
                )
                Toast.makeText(requireContext(), "$rating", Toast.LENGTH_SHORT).show()
                view.btnSendRating.visibility = View.GONE
                binding.textRating.text = "Вы поставили рейтинг к этому продукту"
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.need_sign_up),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }

        view.moreProductsState.setOnClickListener {
            if (isMoreState) {
                isMoreState = false
                view.imgMoreState.setImageResource(R.drawable.bottom_right)
                view.linerProductState.visibility = View.GONE
            } else {
                isMoreState = true
                view.imgMoreState.setImageResource(R.drawable.bottom)
                view.linerProductState.visibility = View.VISIBLE
            }


        }

        view.linerHarakter.setOnClickListener {
            if (isHarakter) {
                isHarakter = false
                view.imgHarakter.setImageResource(R.drawable.bottom_right)
                view.liner2first.visibility = View.GONE
            } else {
                isHarakter = true
                view.imgHarakter.setImageResource(R.drawable.bottom)
                view.liner2first.visibility = View.VISIBLE
            }


        }

        val resultCode1 = 101
        val resultCode2 = 102
        // We can do the assignment inside onAttach or onCreate
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // There are no request codes

                if (result.resultCode == resultCode1 && result.data != null) {
                    activity?.onBackPressed()
                } else if (result.resultCode == resultCode2) {
                    activity?.onBackPressed()
                }
            }


        view.nextAnotherUser.setOnClickListener {

            val intent = Intent(requireActivity(), UserOtherActivity::class.java)
            intent.putExtra("User2_1", nameOtherUser)
            intent.putExtra("User2_2", phoneOtherUser)
            intent.putExtra("User2_3", imageOtherUser)
            intent.putExtra("User2_4", descripOtherUser)
            activityResultLauncher.launch(intent)

            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }


        view.nextPay1.setOnClickListener {
            ID_PAY = 8
            viewModels.getServices()
            alertDialogLoader()
        }

        viewModels.myServices.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                var boolean = false

                for (i in 0 until list?.body()?.data?.size!!) {
                    if (ID_PAY == list.body()?.data?.get(i)?.id) {
                        for (uID in 0 until list.body()?.data?.get(i)?.type!!.size) {

                            if (USER_TYPE == list.body()?.data?.get(i)?.type!![uID]) {
                                map.clear()
                                map["payId"] = ID_PAY.toString()
                                map["productId"] = idProducts.toString()
                                viewModels.payGenerate("Bearer $TOKEN_USER", map)
                                boolean = true
                                break
                            }
                        }
                        break
                    }
                }

                if (!boolean) {
                    dialogLoader.dismiss()
                    alertDialogMessage("Доступ закрыт", "Это услуга вам не доступно")
                }

            } else {
                dialogLoader.dismiss()
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(),
                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }


        }
//
        viewModels.myPayGenerate.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                dialogLoader.dismiss()
                alertDialogPay(
                    list.body()?.data?.order?.description.toString(),
                    "Ваш тариф составляет " +
                            "${list.body()?.data?.order?.price} ${list.body()?.data?.order?.currency} за ${list.body()?.data?.order?.term} дней",
                    list.body()?.data?.url.toString()
                )
            } else {
                dialogLoader.dismiss()
                val jsonObj = JSONObject(list.errorBody()!!.charStream().readText())
                alertDialogMessage(
                    jsonObj.getString("message").toString(), " mm "
//                    jsonObj.getString("errors").toString().replace("[\"", "").replace("\"]", "")
                )
            }
        }

        view.nextPay4.setOnClickListener {
            ID_PAY = 1
            viewModels.getServices()
            alertDialogLoader()
        }
        view.nextPay3.setOnClickListener {
            ID_PAY = 2
            viewModels.getServices()
            alertDialogLoader()
        }
        view.nextPay2.setOnClickListener {
            ID_PAY = 3
            viewModels.getServices()
            alertDialogLoader()
        }

        view.nextComplaintProduct.setOnClickListener {
            val intent = Intent(requireActivity(), ComplaintProductActivity::class.java)
            activityResultLauncher.launch(intent)

            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            view.showDopMenu.visibility = View.GONE
        }

        view.nextCopyLink.setOnClickListener {

            val clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", urlProducts))

//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            uToast(requireContext(), "Copied")

            view.showDopMenu.visibility = View.GONE

        }

        view.updateMessageBtn.setOnClickListener {
            view.showMessage.visibility = View.VISIBLE
            view.showPhoneEmail.visibility = View.GONE
            view.edtNewMessage.requestFocus()
            view.edtNewMessage.showKeyboard()
        }

        view.itemMenu.setOnClickListener {
            if (isMenu) {
                view.showDopMenu.visibility = View.VISIBLE
                isMenu = false
            } else {
                view.showDopMenu.visibility = View.GONE
                isMenu = true
            }
        }


        val autoArMes = resources.getStringArray(R.array.AutoMessageArray)

        autoArMes.forEach { list ->
            autoMessageArray.add(AutoMessageModels(list))
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewModels.myNewMessageModels.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Ваша сообщения было отправлено!",
                    Toast.LENGTH_SHORT
                ).show()
                view.loaderMessage.visibility = View.GONE
                view.rvMessageArray.visibility = View.VISIBLE
                view.showMessage.visibility = View.GONE
                view.showPhoneEmail.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    "Ваша сообщения неотправлено!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        recyclerViewNewMessage = view.rvMessageArray
        adapterAutoMes = AutoMessageAdapter(object : IClickListnearUpdateImage {

            override fun clickListener(baseID: String, index: Int) {
                view.loaderMessage.visibility = View.VISIBLE
                view.rvMessageArray.visibility = View.GONE
                newMessageMap["text"] = baseID
                newMessageMap["product_id"] = getProductID.toString()

                viewModels.newMessage(
                    "Bearer $TOKEN_USER",
                    newMessageMap
                )
                newMessageMap.clear()
            }

        })



        recyclerViewNewMessage.adapter = adapterAutoMes
        recyclerViewNewMessage.setHasFixedSize(true)
        adapterAutoMes.setList(autoMessageArray)


        view.btnPostMessage.setOnClickListener {

            if (view.edtNewMessage.text.isNotEmpty()) {
                newMessageMap["text"] = view.edtNewMessage.text.toString()
                newMessageMap["product_id"] = getProductID.toString()

                viewModels.newMessage(
                    "Bearer $TOKEN_USER",
                    newMessageMap
                )
                newMessageMap.clear()
                view.edtNewMessage.text.clear()
                view.root.hideKeyboard()
                dialog.hide()
            } else {
                uToast(requireContext(), resources.getString(R.string.null_string))
            }


        }

        view.exitDialog.setOnClickListener {
            view.showMessage.visibility = View.GONE
            view.showPhoneEmail.visibility = View.VISIBLE
        }


        return view.root
    }

    private fun removeItem(array: ArrayList<Int>, value: Int): ArrayList<Int> {
        val arr = array.filter { it != value }.toIntArray()
        return arr.toCollection(ArrayList())
    }

    private fun isContains(array: ArrayList<Int>, value: Int): Boolean {
        return array.contains(value)
    }


    private fun isStateSizeShow() {
        if (compareAll.size > 0) {
            binding.updateImageCompare.visibility = View.VISIBLE
            binding.boardSize.text = compareAll.size.toString()
        } else {
            binding.updateImageCompare.visibility = View.GONE
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonCompare.visibility = View.VISIBLE
            binding.floatingActionButtonReview.visibility = View.VISIBLE
            binding.floatingActionButtonShare.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonCompare.visibility = View.INVISIBLE
            binding.floatingActionButtonReview.visibility = View.INVISIBLE
            binding.floatingActionButtonShare.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonCompare.startAnimation(rotateFrom)
            binding.floatingActionButtonReview.startAnimation(rotateFrom)
            binding.floatingActionButtonShare.startAnimation(rotateFrom)
            binding.fabId.startAnimation(rotateOpen)
        } else {
            binding.floatingActionButtonCompare.startAnimation(rotateTo)
            binding.floatingActionButtonReview.startAnimation(rotateTo)
            binding.floatingActionButtonShare.startAnimation(rotateTo)
            binding.fabId.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonCompare.isClickable = true
            binding.floatingActionButtonReview.isClickable = true
            binding.floatingActionButtonShare.isClickable = true
        } else {
            binding.floatingActionButtonCompare.isClickable = false
            binding.floatingActionButtonReview.isClickable = false
            binding.floatingActionButtonShare.isClickable = false
        }
    }

    // Похожие
    private fun refReview() {
        viewModels.getSimilar("Bearer $TOKEN_USER", idProducts)
        viewModels.mySimilar.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                list.body()?.data?.let { adapterReview.setList(it) }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.updateAll.visibility = View.GONE
        binding.itemFavorite.visibility = View.GONE
        binding.updateMessageBtn.visibility = View.GONE
        binding.updateCallBtn.visibility = View.GONE
        binding.dopTextUpdate.visibility = View.INVISIBLE
        binding.UpdateWorkError.visibility = View.VISIBLE
        viewModels.getUpdateDataArrayUpdate("Bearer $TOKEN_USER", idProducts)

        try {

            isStateSizeShow()
            viewModels.myShowProducts.observe(viewLifecycleOwner) { list ->

                if (list.isSuccessful) {

                    val test = ArrayList<Image>()
                    test.add(Image(0, "null", list.body()?.data?.img.toString(), "null"))
                    list.body()?.data?.images?.let { test.addAll(it) }
                    adapter.setListImage(test)


                    if (list?.body()?.data?.show_fields == true){
                        fieldsUpload()
                    }else{
                        binding.textHarakteristic.text = "Нет информации о характеристиках."
                    }


                    if (list.body()?.data?.myProduct == 1) {
                        binding.statePay.visibility = View.VISIBLE
                        binding.updateCallBtn.visibility = View.GONE
                        binding.updateMessageBtn.visibility = View.GONE
                    } else {
                        binding.updateCallBtn.visibility = View.VISIBLE
                        binding.updateMessageBtn.visibility = View.VISIBLE
                    }

                    if (list.body()?.data?.price == 0) {
                        binding.updatePrice.text = resources.getText(R.string.negotiable)
                        binding.teng.visibility = View.GONE
                    } else {
                        binding.updatePrice.text = list.body()?.data?.price.toString()
                    }


                    binding.textDescription.text = list.body()?.data?.description
                    binding.updateCompany.text = list.body()?.data?.name
                    nameProducts = list.body()?.data?.name.toString()
                    descProduct = list.body()?.data?.description.toString()
                    urlProducts = url + list.body()?.data?.id.toString()
                    if (list.body()?.data?.categories?.isNotEmpty() == true) {
                        binding.textCategory.text = list.body()?.data?.categories?.get(0)?.name
                        idCategory = list.body()?.data?.categories!![0].id
                    } else {
                        binding.textCategory.text = resources.getText(R.string.not_categorized)
                        idCategory = -1
                    }

                    if (list.body()?.data?.brand != null) {
                        binding.textBrand.text = list.body()?.data?.brand?.name
                    } else {
                        binding.textBrandBase.visibility = View.GONE
                        binding.textBrand.visibility = View.GONE
                    }


                    binding.textLocationInfo.text =
                        list.body()?.data?.city?.parent?.name + " -> " + list.body()?.data?.city?.name
                    if (list.body()?.data?.state == 1) {
                        binding.textState.text = resources.getText(R.string.new_products)
                    } else {
                        binding.textState.text = resources.getText(R.string.boo_products)
                    }

                    if (list.body()?.data?.ratingsAvg == null) {
                        binding.svgRating.rating = 0.0f
                        binding.numberRatingAVG.text = resources.getText(R.string.not_rating)
                    } else {
                        binding.svgRating.rating = list.body()?.data?.ratingsAvg!!
                        binding.numberRatingAVG.text = binding.svgRating.rating.toString()
                    }

                    binding.textShop.text = list.body()?.data?.shop?.name

                    if (list.body()?.data?.user?.type == 2) {
                        binding.nameState.visibility = View.GONE
                        binding.textState.visibility = View.GONE
                        binding.nameBuy.visibility = View.GONE
                        binding.textNameUser.visibility = View.GONE
                        binding.nameNumberPhone.text = resources.getText(R.string.phone_number)
                        binding.textNumberUser.text = list.body()?.data?.user?.phone
                    } else {
                        binding.textNameUser.text = list.body()?.data?.user?.name
                        binding.textNumberUser.text = list.body()?.data?.user?.phone
                    }


                    if (list.body()?.data?.review != 0) {
                        isReview = true
                    }


                    if (list.body()?.data?.isRating == null) {
                        binding.isRatingUser.rating = 0.0f
                        binding.btnSendRating.visibility = View.VISIBLE
                    } else {
                        binding.isRatingUser.rating = list.body()?.data?.isRating!!
                        binding.textRating.text = "Вы поставили рейтинг к этому продукту"
                        binding.btnSendRating.visibility = View.GONE
                    }


                    if (list.body()?.data?.user?.phone?.isNotEmpty()!!) {
                        userTelephony = list.body()?.data?.user?.phone!!
                    }


                    MyUtils.uGlide(requireContext(), binding.imgAddFirst, test[indexShow].name)

                    isLike = if (list.body()?.data?.isLike == false) {
                        binding.updateImageFavorite.setImageResource(R.drawable.ic_favorite)
                        false
                    } else {
                        binding.updateImageFavorite.setImageResource(R.drawable.ic_favorite2)
                        true
                    }

                    if (list.body()?.data?.installment != null) {
                        binding.linerShowInstalment.visibility = View.VISIBLE
                        binding.txtInsPrice.text =
                            list.body()?.data?.installment?.price.toString()
                        binding.txtFirstPercentPrice.text =
                            "${list.body()?.data?.installment?.firstPercent.toString()}% -> ${list.body()?.data?.installment?.first.toString()} ${
                                resources.getText(
                                    R.string.tenge
                                )
                            } "
                        binding.txtPriceMonth.text =
                            "${list.body()?.data?.installment?.type.toString()} ${
                                resources.getText(
                                    R.string.month_to
                                )
                            } ${list.body()?.data?.installment?.month.toString()} ${
                                resources.getText(
                                    R.string.tenge
                                )
                            } "
                    } else {
                        binding.stateInstalment.text = resources.getText(R.string.not)
                    }

                    binding.idProduct.text = " " + list.body()?.data?.id.toString()
                    binding.idCreate.text = " " + list.body()?.data?.created_at.toString()
                    binding.idUpdate.text = " " + list.body()?.data?.updated_at.toString()
                    binding.anotherUserName.text = list.body()?.data?.shop?.name
                    binding.anotherUserPhone.text = list.body()?.data?.user?.email
                    MyUtils.uGlide(
                        requireContext(),
                        binding.anotherUserImage,
                        list.body()?.data?.shop?.img
                    )

                    nameOtherUser = list.body()?.data?.shop?.name.toString()
                    phoneOtherUser = list.body()?.data?.user?.phone.toString()
                    imageOtherUser = list.body()?.data?.shop?.img.toString()
                    descripOtherUser = list.body()?.data?.shop?.description.toString()
                    USER_OTHER_ID = list.body()?.data?.shop?.id!!

                    if (list.body()?.data?.verified == 0) {
                        binding.stVerification.visibility = View.GONE

                    } else {
                        binding.stVerification.visibility = View.VISIBLE

                    }


                    if (list.body()?.data?.fast != null) {
                        if (getDateTime(list.body()?.data?.fast.toString())) {
                            binding.stFast.visibility = View.GONE
                            binding.nextPay2.visibility = View.VISIBLE
                        } else {
                            binding.stFast.visibility = View.VISIBLE
                            binding.nextPay2.visibility = View.GONE
                        }
                    } else {
                        binding.stFast.visibility = View.GONE
                        binding.nextPay2.visibility = View.VISIBLE
                    }

                    if (list.body()?.data?.top != null) {
                        if (getDateTime(list.body()?.data?.top.toString())) {
                            binding.stTop.visibility = View.GONE
                            binding.nextPay3.visibility = View.VISIBLE
                        } else {
                            binding.stTop.visibility = View.VISIBLE
                            binding.nextPay3.visibility = View.GONE
                        }
                    } else {
                        binding.stTop.visibility = View.GONE
                        binding.nextPay3.visibility = View.VISIBLE
                    }

                    if (list.body()?.data?.vip_status != null) {
                        if (getDateTime(list.body()?.data?.vip_status.toString())) {
                            binding.stVIP.visibility = View.GONE
                            binding.nextPay1.visibility = View.VISIBLE
                        } else {
                            binding.stVIP.visibility = View.VISIBLE
                            binding.nextPay1.visibility = View.GONE
                        }
                    } else {
                        binding.stVIP.visibility = View.GONE
                        binding.nextPay1.visibility = View.VISIBLE
                    }


                    if (list.body()?.data?.before_date != null) {
                        if (getDateTime(list.body()?.data?.before_date.toString())) {
                            binding.nextPay4.visibility = View.VISIBLE
                        } else {
                            binding.nextPay4.visibility = View.GONE
                        }
                    } else {
                        binding.nextPay4.visibility = View.VISIBLE
                    }


                    binding.updateAll.visibility = View.VISIBLE
                    binding.itemFavorite.visibility = View.VISIBLE
                    binding.UpdateWorkError.visibility = View.GONE

                } else {
                    binding.updateAll.visibility = View.GONE
                    binding.UpdateWorkError.visibility = View.VISIBLE
                }

            }

            viewModels.getReviews(idProducts)
            viewModels.myReviews.observe(viewLifecycleOwner) { user ->
                if (user.isSuccessful) {
                    binding.textReviewUpdate.text =
                        "(${user.body()!!.meta.total} ${resources.getText(R.string.reviews)})"
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding.updateAll.visibility = View.GONE
            binding.itemFavorite.visibility = View.GONE
            binding.updateMessageBtn.visibility = View.GONE
            binding.updateCallBtn.visibility = View.GONE
            binding.UpdateWorkError.visibility = View.VISIBLE
            binding.dopTextUpdate.visibility = View.VISIBLE
        }
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$userTelephony")
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun fieldsUpload() {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val layoutText2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        )

        viewModels.myShowProducts.observe(viewLifecycleOwner) { res ->

            binding.fieldsUploadUpdate.removeAllViews()
            ed.clear()

            for (i in 0 until res.body()?.data?.fields?.size!!.toInt()) {
                val linearLayout2 = LinearLayout(binding.fieldsUploadUpdate.context)
                linearLayout2.removeAllViews()
                linearLayout2.orientation = LinearLayout.VERTICAL
                linearLayout2.layoutParams = layoutParams

                val textViewName = TextView(linearLayout2.context)
                val viewLine = View(linearLayout2.context)



                if (res.body()?.data?.fields?.get(i)?.type == "number" ||
                    res.body()?.data?.fields?.get(i)?.type == "rate" ||
                    res.body()?.data?.fields?.get(i)?.type == "string"
                ) {


                    viewLine.layoutParams = layoutView
                    viewLine.marginTop.div(15)
                    viewLine.background = requireContext().resources.getDrawable(R.color.item)


                    textViewName.text = res.body()?.data?.fields?.get(i)?.name
                    textViewName.textSize = 11f
                    textViewName.layoutParams = layoutText
                    textViewName.typeface = Typeface.DEFAULT_BOLD


                    val textViewValue = TextView(linearLayout2.context)
                    textViewValue.textSize = 14f
                    textViewValue.text = res.body()?.data?.fields?.get(i)!!.valueUser
                    textViewValue.layoutParams = layoutText2
                    textViewValue.gravity = Gravity.CENTER

                    val paramTextViewName =
                        (textViewName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                            setMargins(0, 25, 0, 0)
                        }
                    textViewName.layoutParams = paramTextViewName


                    val paramViewLine =
                        (viewLine.layoutParams as ViewGroup.MarginLayoutParams).apply {
                            setMargins(0, 25, 0, 0)
                        }
                    viewLine.layoutParams = paramViewLine

                    linearLayout2.addView(viewLine)
                    linearLayout2.addView(textViewName)
                    linearLayout2.addView(textViewValue)

                    binding.fieldsUploadUpdate.addView(linearLayout2)

                    ed.add(textViewValue)
                }

                if (res.body()?.data?.fields?.get(i)?.type == "select") {

                    viewLine.layoutParams = layoutView
                    viewLine.background = requireContext().resources.getDrawable(R.color.item)


                    textViewName.text = res.body()?.data?.fields?.get(i)?.name
                    textViewName.textSize = 11f
                    textViewName.layoutParams = layoutText
                    textViewName.typeface = Typeface.DEFAULT_BOLD


                    val textViewString = TextView(linearLayout2.context)
                    textViewString.layoutParams = layoutText2
                    textViewString.textSize = 14f

                    res.body()?.data?.fields?.get(i)?.valueUser?.let { value ->

                        textViewString.text = value

                    }

                    val paramTextViewName =
                        (textViewName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                            setMargins(0, 25, 0, 0)
                        }
                    textViewName.layoutParams = paramTextViewName


                    val paramViewLine =
                        (viewLine.layoutParams as ViewGroup.MarginLayoutParams).apply {
                            setMargins(0, 25, 0, 0)
                        }
                    viewLine.layoutParams = paramViewLine

                    linearLayout2.addView(viewLine)
                    linearLayout2.addView(textViewName)
                    linearLayout2.addView(textViewString)

                    binding.fieldsUploadUpdate.addView(linearLayout2)
                    ed.add(textViewString)
                }
            }
            if (ed.size == 0) {
                binding.linerHarakter.visibility = View.GONE
            }
        }
    }

    private fun showSharingDialogAsKotlinWithURL(title: String,text: String, url: String) {
//        val intent = Intent()
//        intent.action = Intent.ACTION_SEND
//        intent.type = "text/plain"
//        intent.putExtra(Intent.EXTRA_TEXT, "$text: $url")
//        startActivity(Intent.createChooser(intent, "Share with:"))

        CoroutineScope(Dispatchers.IO).launch {
            val urlHTML = url
            val doc = Jsoup.connect(urlHTML).get()
            val titleHTML = doc.title()
            val descriptionHTML = doc.select("meta[name=description]").attr("content")

            // Do something with the HTML data
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, titleHTML)
            intent.putExtra(Intent.EXTRA_TEXT, "$titleHTML\n\n$descriptionHTML\n\n$url")
            startActivity(Intent.createChooser(intent, "Share with:"))
        }

    }

    private fun alertDialogMessage(title: String, descrip: String) {

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_error_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialog.findViewById<Button>(R.id.dialog_yes_error)
        val textTitle = dialog.findViewById<TextView>(R.id.txt_title_error)
        val textDescrip = dialog.findViewById<TextView>(R.id.txt_descript_error)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun alertDialogPay(title: String, descrip: String, url: String) {

        dialogPay.setCancelable(false)
        dialogPay.setCanceledOnTouchOutside(false)
        dialogPay.setContentView(R.layout.dialog_pay)
        dialogPay.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonYES = dialogPay.findViewById<Button>(R.id.dialog_pay_yes)
        val buttonNOT = dialogPay.findViewById<Button>(R.id.dialog_pay_no)
        val textTitle = dialogPay.findViewById<TextView>(R.id.txt_title_pay)
        val textDescrip = dialogPay.findViewById<TextView>(R.id.txt_descript_pay)
        textTitle.text = title
        textDescrip.text = descrip
        buttonYES.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
            dialogPay.dismiss()
        }
        buttonNOT.setOnClickListener {
            dialogPay.dismiss()
        }
        dialogPay.show()
    }

    private fun alertDialogLoader() {

        dialogLoader.setCancelable(false)
        dialogLoader.setCanceledOnTouchOutside(false)
        dialogLoader.setContentView(R.layout.dialog_loader)
        dialogLoader.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoader.show()

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): Boolean {
        //Сегодняшняя дата
        val currentTime: Date = Calendar.getInstance().time
        //Преобразование приходяшее дата
        val dateParse = SimpleDateFormat("dd.MM.yyyy").parse(s) as Date

        return dateParse.time <= currentTime.time
    }

    private fun View.showKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}