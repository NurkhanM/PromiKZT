package product.promikz.screens.hometwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import product.promikz.*
import product.promikz.AppConstants.APP_PREFERENCES
import product.promikz.AppConstants.FILTERS_TYPE
import product.promikz.AppConstants.ID_SHOP_MY
import product.promikz.AppConstants.PROGRESS_COUNT
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.followBannerString
import product.promikz.AppConstants.getSpecialistIDSTATE
import product.promikz.AppConstants.imagesStoryAll
import product.promikz.AppConstants.imagesStoryFollow
import product.promikz.AppConstants.totalCountProduct
import product.promikz.AppConstants.totalCountSchool
import product.promikz.AppConstants.totalCountSpecialist
import product.promikz.AppConstants.totalNotification
import product.promikz.AppConstants.userIDChat
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.databinding.FragmentHomeTwoBinding
import product.promikz.databinding.ItemHomeViewRatingBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFavorite
import product.promikz.inteface.IClickListnearHomeRating
import product.promikz.inteface.IClickListnearHomeStory
import product.promikz.models.test.storeGaid.GaidModels
import product.promikz.screens.hometwo.adapters.GaidAdapter
import product.promikz.screens.hometwo.adapters.ViewsRatingAdapter
import product.promikz.screens.hometwo.story.HomeTwoAdapter
import product.promikz.screens.hometwo.story.StoryActivity
import product.promikz.screens.kursy.KursyActivity
import product.promikz.screens.notifications.index.NotificationsActivity
import product.promikz.screens.pageErrorNetworks.TokenErrorActivity
import product.promikz.screens.search.products.SSortActivity
import product.promikz.screens.specialist.SpecialistActivity
import product.promikz.screens.update.UpdateActivity
import product.promikz.viewModels.HomeViewModel
import product.promikz.viewModels.ProfileViewModel
import kotlin.math.abs


class HomeTwoFragment : Fragment() {


    private var _binding: FragmentHomeTwoBinding? = null
    private val binding get() = _binding!!

    private var activityBinding: ActivityMainBinding? = null

    private var imageList = mutableListOf<String>()
    private lateinit var recyclerViews: RecyclerView
    private lateinit var recyclerViewStory: RecyclerView
    private lateinit var recyclerRatingAvg: RecyclerView
    private lateinit var recyclerGaid: RecyclerView

    private lateinit var adapterStory: HomeTwoAdapter
    private lateinit var adapterViews: ViewsRatingAdapter
    private lateinit var adapterRatingAvg: ViewsRatingAdapter
    private lateinit var adapterGaid: GaidAdapter

    private var currentPagerSize = 1

    private lateinit var preferencesTheme: SharedPreferences
    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mProfileViewModel: ProfileViewModel


    lateinit var slideHandler: Handler
    lateinit var slideRunnable: Runnable


    override fun onStart() {
        super.onStart()
        mHomeViewModel.getStory()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mProfileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        mHomeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeTwoBinding.inflate(inflater, container, false)
        val view = binding


        view.htNotification.setOnClickListener {
            askNotificationPermission()
        }

        view.btnHomeAllStanki.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_nav_view_menu_home_to_twoAdvertFragment)
        }

        view.btnHomeAllSpecialist.setOnClickListener {
            val intent = Intent(requireActivity(), SpecialistActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        view.btnHomeAllUcheb.setOnClickListener {
            val intent = Intent(requireActivity(), KursyActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }


        recyclerViewStory = view.rvStory
        adapterStory = HomeTwoAdapter(object : IClickListnearHomeStory {
            override fun clickListener(pos: Int, show: String) {


                mHomeViewModel.getStoryShow(pos.toString())
                mHomeViewModel.myStory.observe(viewLifecycleOwner) { response ->
                    loop@ for (i in 0 until response.body()?.data?.size!!) {
                        if (response.body()?.data!![i].id == pos) {
                            if (response.body()?.data!![i].images.isNotEmpty()) {
                                PROGRESS_COUNT = response.body()?.data!![i].images.size
                                imagesStoryAll.clear()
                                imagesStoryFollow.clear()
                                for (k in 0 until response.body()?.data!![i].images.size) {
                                    imagesStoryAll.add(response.body()?.data!![i].images[k].name)
                                    imagesStoryFollow.add(response.body()?.data!![i].images[k].link.toString())
                                }
                            } else {
                                PROGRESS_COUNT = 1
                                imagesStoryAll.clear()
                                imagesStoryFollow.clear()
                                imagesStoryAll.add("https://www.mordeo.org/files/uploads/2022/05/The-Boys-Dawn-of-The-Seven-Series-Poster-4K-Ultra-HD-Mobile-Wallpaper.jpg")
                                imagesStoryFollow.add("https://www.mordeo.org/files/uploads/2022/05/The-Boys-Dawn-of-The-Seven-Series-Poster-4K-Ultra-HD-Mobile-Wallpaper.jpg")
                            }

                            break@loop
                        }
                    }
                }

                val intent = Intent(requireActivity(), StoryActivity::class.java)
                intent.putExtra("story", pos)
                intent.putExtra("show", show)
                startActivity(intent)
                activity?.overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
            }

        })

        recyclerViewStory.adapter = adapterStory
        recyclerViewStory.setHasFixedSize(true)

        recyclerViews = view.rvViews
        adapterViews = ViewsRatingAdapter(
            object : IClickListnearHomeRating {

                override fun clickListener(
                    baseID: Int,
                    viewAdapter: ItemHomeViewRatingBinding,
                    isLike: Boolean,
                    position: Int
                ) {
                    if (!isLike) {
                        viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                    } else {
                        viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                    }

                    mHomeViewModel.postLike("Bearer $TOKEN_USER", baseID)
                }

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
                        } catch (e: ActivityNotFoundException) {
                            uToast(requireContext(), resources.getString(R.string.error_link))
                        }
                    }
                }

            })

        recyclerViews.adapter = adapterViews
        recyclerViews.setHasFixedSize(true)



        recyclerRatingAvg = view.rvRatingAvg
        adapterRatingAvg = ViewsRatingAdapter(
            object : IClickListnearHomeRating {

                override fun clickListener(
                    baseID: Int,
                    viewAdapter: ItemHomeViewRatingBinding,
                    isLike: Boolean,
                    position: Int
                ) {
                    if (!isLike) {
                        viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                    } else {
                        viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                    }

                    mHomeViewModel.postLike("Bearer $TOKEN_USER", baseID)
                }

                override fun clickListener2(baseID: Int, adver: Int, favorite: String) {
                    if (adver == 0 || adver == 1) {
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
                        } catch (e: ActivityNotFoundException) {
                            uToast(requireContext(), resources.getString(R.string.error_link))
                        }
                    }
                }

            })

        recyclerRatingAvg.adapter = adapterRatingAvg
        recyclerRatingAvg.setHasFixedSize(true)



        recyclerGaid = view.rvGaid
        adapterGaid = GaidAdapter(
            object : IClickListnearHomeFavorite {

                override fun clickListener(
                    baseID: Int,
                    viewAdapter: ItemMakeupModelsBinding,
                    isLike: Boolean,
                    position: Int
                ) {
                    //todo
                }

                override fun clickListener2(baseID: Int, adver: Int, favorite: String) {

//                        val intent = Intent(requireActivity(), GaidActivity::class.java)
//                        intent.putExtra("gaid", baseID)
//                        startActivity(intent)
//                        (activity as AppCompatActivity).overridePendingTransition(
//                            R.anim.zoom_enter,
//                            R.anim.zoom_exit)

                    when (baseID) {
                        0 -> {
                            Navigation.findNavController(view.root)
                                .navigate(R.id.action_nav_view_menu_home_to_createShopGaidFragment)
                        }

                        1 -> {
                            Navigation.findNavController(view.root)
                                .navigate(R.id.action_nav_view_menu_home_to_gaidPayFragment)
                        }

                        2 -> {
                            Navigation.findNavController(view.root)
                                .navigate(R.id.action_nav_view_menu_home_to_gaidAdsFragment)
                        }

                        else -> {
                            Navigation.findNavController(view.root)
                                .navigate(R.id.action_nav_view_menu_home_to_gaidAuthFragment)
                        }
                    }


                }

            })

        recyclerGaid.adapter = adapterGaid
        recyclerGaid.setHasFixedSize(true)

        val arGaid = arrayListOf(
            GaidModels(R.drawable.ic_gaid_store, "Создание\nмагазина"),
            GaidModels(R.drawable.ic_gaid_pay, "Платные\nуслуги"),
            GaidModels(R.drawable.ic_gaid_ads, "Объявления"),
            GaidModels(R.drawable.ic_gaid_auth, "Авторизация")
        )

        adapterGaid.setList(arGaid)




        slideHandler = Handler()
        slideRunnable = Runnable {
            view.viewPagerHome.currentItem = view.viewPagerHome.currentItem + 1
        }
        postToList()


//        mHomeViewModel.getStory()
        mHomeViewModel.myStory.observe(viewLifecycleOwner)
        { response ->

            if (response.body()?.data?.isNotEmpty() == true) {
                response.body()?.data?.let { adapterStory.setList(it) }
            } else {
                view.homeTxtStory.visibility = View.GONE
            }

        }

        ratingAvgGetRequest()


        preferencesTheme = (activity as AppCompatActivity).getSharedPreferences(
            APP_PREFERENCES,
            Context.MODE_PRIVATE
        )


//        if (preferencesTheme.getString(KEY_THEME, "error_theme").toString() == "white") {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        } else if (preferencesTheme.getString(KEY_THEME, "error_theme").toString() == "black") {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }


        view.txtAllStanok.text = "${resources.getText(R.string.total)} $totalCountProduct"
        view.txtAllUcheb.text = "${resources.getText(R.string.total)} $totalCountSchool"
        view.txtAllSpecialist.text = "${resources.getText(R.string.total)} $totalCountSpecialist"


        view.homeConstreit1.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
            FILTERS_TYPE = ""
        }




        return view.root
    }


    private fun viewsGetRequest() {

        mHomeViewModel.getSortView("Bearer $TOKEN_USER", "views;desc")
        mHomeViewModel.mySortView.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                list.body()?.data?.let { adapterViews.setList(it) }
            }
        }
    }

    private fun ratingAvgGetRequest() {

        mHomeViewModel.getSortRating("Bearer $TOKEN_USER", "ratingsAvg;desc")
        mHomeViewModel.mySortRating.observe(viewLifecycleOwner) { list ->
            if (list.isSuccessful) {
                list.body()?.data?.let { adapterRatingAvg.setList(it) }
            }
        }
    }


    private fun postToList() {

        mHomeViewModel.setBannerPost()

        mHomeViewModel.getBannerPost.observe(viewLifecycleOwner) { response ->
            currentPagerSize = response.body()!!.data.size
            followBannerString.clear()
            if (response.body()!!.data.isNotEmpty()) {
                for (i in 0 until response.body()!!.data.size) {
                    imageList.add(response.body()!!.data[i].img)
                    followBannerString.add(response.body()!!.data[i].link.toString())
                }

                binding.viewPagerHome.adapter = ViewPagerAdapterAdvert(imageList)
                binding.viewPagerHome.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.indicatorCircular.setViewPager(binding.viewPagerHome)
                binding.viewPagerHome.clipToPadding = false
                binding.viewPagerHome.clipChildren = false
                binding.viewPagerHome.offscreenPageLimit = 3
                binding.viewPagerHome.getChildAt(0)?.overScrollMode =
                    RecyclerView.FOCUSABLES_TOUCH_MODE

                val pagerPadding = 80
                binding.viewPagerHome.setPadding(pagerPadding, 20, pagerPadding, 30)


                val comPosPageTarn = CompositePageTransformer()
                comPosPageTarn.addTransformer(MarginPageTransformer(30))
                comPosPageTarn.addTransformer { page, possition ->
                    val r: Float = 1 - abs(possition)
                    page.scaleY = 0.90f + r * 0.15f
                }
                binding.viewPagerHome.setPageTransformer(comPosPageTarn)


                binding.viewPagerHome.registerOnPageChangeCallback(
                    object : ViewPager2.OnPageChangeCallback() {

                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)

                            if (position == (currentPagerSize - 1)) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(5000)
                                    binding.viewPagerHome.setCurrentItem(0, true)
                                }

                            }
                            slideHandler.removeCallbacks(slideRunnable)
                            slideHandler.postDelayed(slideRunnable, 5000)
                        }

                    })
            } else {
                binding.homeConstreit2.visibility = View.GONE
            }

        }


    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(slideRunnable, 5000)
        userUpdate()
        viewsGetRequest()
        if (totalNotification == 0) {
            binding.boardSize.visibility = View.GONE
        } else {
            binding.boardSize.text = totalNotification.toString()
        }

    }


    private fun userUpdate() {
        mHomeViewModel.getUser("Bearer $TOKEN_USER")
        mHomeViewModel.myUser.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                userIDChat = response.body()?.data?.id
                ID_SHOP_MY = if (response.body()?.data?.shop == null) {
                    -1
                } else {
                    response.body()?.data?.shop?.id!!
                }

                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }

                    val token = task.result
                    mHomeViewModel.sendNotify("Bearer $TOKEN_USER", "put", token)
                }

                if (response.body()?.data?.specialist != null) {
                    getSpecialistIDSTATE = true
                }


                val navView = activityBinding?.navView
                val hView: View = navView!!.getHeaderView(0)
                val navMenu: Menu = navView.menu
                hView.findViewById<TextView>(R.id.user_name).text =
                    response.body()?.data?.name
                hView.findViewById<TextView>(R.id.user_emile).text =
                    response.body()?.data?.email
                //base
                navMenu.findItem(R.id.nav_view_menu_home).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_sravnit).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_stanki).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_specialist).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_kurs).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_notifications).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_company).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_setting).isVisible = true

                //accaunt
                navMenu.findItem(R.id.nav_view_menu_favorite).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_subscriber).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_login).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_chad).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_buy).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_my_shop).isVisible = true
                navMenu.findItem(R.id.nav_view_menu_exit_account).isVisible = true

                val imageView: ImageView = hView.findViewById(R.id.image_circule_nav)
                val linContainer: LinearLayout = hView.findViewById(R.id.avatarLinear)
                val progressBar: ProgressBar = hView.findViewById(R.id.avatarProgress)
                progressBar.visibility = View.GONE
                linContainer.visibility = View.VISIBLE
                MyUtils.uGlide(requireContext(), imageView, response.body()?.data?.img)

            } else {
                val intent = Intent(requireActivity(), TokenErrorActivity::class.java)
                startActivity(intent)
                (activity as AppCompatActivity).overridePendingTransition(
                    R.anim.zoom_enter,
                    R.anim.zoom_exit
                )
                (activity as AppCompatActivity).finish()
            }
        }
    }


    private fun nextNotificationState() {
        val intent = Intent(requireActivity(), NotificationsActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }


    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            uToast(requireContext(), "Вы включили уведомление")
            nextNotificationState()
        } else {
            uToast(requireContext(), "Вы отключили уведомление")
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                nextNotificationState()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                uToast(requireContext(), "Уведомление помощи")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            nextNotificationState()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылку на ViewBinding активити
        activityBinding = (requireActivity() as? MainActivity)?.binding

        // Используем ссылку на ViewBinding активити, чтобы получить доступ к View
        binding.htDrawer.setOnClickListener {
            activityBinding?.layoutDrawer?.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activityBinding = null
    }

}