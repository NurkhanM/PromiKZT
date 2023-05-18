package product.promikz

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import product.promikz.AppConstants.userImageActivity
import product.promikz.screens.company.CompanyActivity
import product.promikz.screens.favorite.FavoriteActivity
import product.promikz.screens.notifications.index.NotificationsActivity
import product.promikz.screens.onlineChat.ChatOnlineActivity
import product.promikz.screens.refresh.RefreshActivity
import product.promikz.screens.settings.SettingsActivity
import product.promikz.screens.shop.ShopsActivity
import product.promikz.screens.specialist.SpecialistActivity
import product.promikz.screens.sravnit.CompareActivity
import product.promikz.screens.user.UserActivity
import com.google.android.material.navigation.NavigationView
import product.promikz.AppConstants.CATEGORY_INT_SEARCH_DATA
import product.promikz.MyUtils.uGlide
import product.promikz.MyUtils.uLogD
import product.promikz.MyUtils.uToast
import product.promikz.databinding.ActivityMainBinding
import product.promikz.screens.kursy.KursyActivity
import product.promikz.screens.pay.PayActivity
import product.promikz.screens.subscriber.SubscriberActivity


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var controller: NavController

    private lateinit var pushBroadcastReceiver: BroadcastReceiver

    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Test commit
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (isConnected) {
                binding.disconnect.root.visibility = View.GONE
                binding.connect.visibility = View.VISIBLE
            } else {
                binding.disconnect.root.visibility = View.VISIBLE
                binding.connect.visibility = View.GONE
            }
        }


        controller = findNavController(R.id.nav_host)

        pushBroadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras
                extras?.keySet()?.firstOrNull{ it == AppConstants.KEY_ACTION}?.let { key ->
                    when(extras.getString(key)){
                        AppConstants.ACION_SHOW_MESSAGE -> {
                            extras.getString(AppConstants.KEY_MESSAGE)?.let { message ->
                                uToast(applicationContext, message)
                            }
                        }
                        else -> uLogD("ERROR -> No needed key found")
                    }

                }
            }

        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.INTENT_FILTER)

        registerReceiver(pushBroadcastReceiver, intentFilter)

        setSupportActionBar(binding.toAction.myToolbar)
        supportActionBar?.title = ""
        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.layoutDrawer)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)

        binding.toAction.myToolbar.setPadding(0, 0, 0, 0)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)

        val navController = Navigation.findNavController(this, R.id.nav_host)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navView.itemIconTintList = null
        val hView: View = navView.getHeaderView(0)
        hView.findViewById<TextView>(R.id.user_name).text =
            resources.getText(R.string.auth_create).toString()
        hView.findViewById<TextView>(R.id.user_emile).text = ""
        val navMenu: Menu = navView.menu

        navMenu.findItem(R.id.nav_view_menu_home).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_sravnit).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_stanki).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_specialist).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_kurs).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_notifications).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_company).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_setting).isVisible = false

        navMenu.findItem(R.id.nav_view_menu_favorite).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_subscriber).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_login).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_chad).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_buy).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_my_shop).isVisible = false
        navMenu.findItem(R.id.nav_view_menu_exit_account).isVisible = false
        val imageView: ImageView = hView.findViewById(R.id.image_circule_nav)

        uGlide(this, imageView, userImageActivity)


        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        navView.setNavigationItemSelectedListener {

            binding.toAction.homeBack.visibility = View.GONE
            when (it.itemId) {
                R.id.nav_view_menu_home -> {
                    binding.toAction.fragmentGraph.visibility = View.VISIBLE
                }

                R.id.nav_view_menu_favorite -> {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_subscriber -> {
                    val intent = Intent(this, SubscriberActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }


                R.id.nav_view_menu_sravnit -> {
                    val intent = Intent(this, CompareActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_stanki -> {
                    Navigation.findNavController(this, R.id.nav_host)
                        .navigate(R.id.action_nav_view_menu_home_to_twoAdvertFragment)
                    it.isChecked = false
                    it.isCheckable = false
                }
                R.id.nav_view_menu_specialist -> {
                    val intent = Intent(this, SpecialistActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }
                R.id.nav_view_menu_kurs -> {
                    val intent = Intent(this, KursyActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_buy -> {
                    val intent = Intent(this, PayActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.zoom_enter,
                        R.anim.zoom_exit
                    )
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_chad -> {
                    val intent = Intent(this, ChatOnlineActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_notifications -> {
                    val intent = Intent(this, NotificationsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }

                R.id.nav_view_menu_company -> {
                    val intent = Intent(this, CompanyActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }
                R.id.nav_view_menu_login -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }
                R.id.nav_view_menu_my_shop -> {
                    val intent = Intent(this, ShopsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }
                R.id.nav_view_menu_exit_account -> {
                    val intent = Intent(this, RefreshActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                    this.finish()
                }
                R.id.nav_view_menu_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    it.isChecked = false
                    it.isCheckable = false
                }
            }

            drawerLayout.closeDrawer(START)
            true
        }

        binding.toAction.homeBack.setOnClickListener {
            super.onBackPressed()
        }

        drawerLayout.setDrawerListener(object : DrawerLayout.SimpleDrawerListener() {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                drawerLayout.requestFocus().not()

            }
        })


    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if (destination.id == R.id.nav_view_menu_home) {
            CATEGORY_INT_SEARCH_DATA = 0
        }
    }

    override fun onResume() {
        super.onResume()
        controller.addOnDestinationChangedListener(listener)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {


        if (drawerLayout.isDrawerOpen((START))) {
            drawerLayout.closeDrawer(START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pushBroadcastReceiver)
    }

}