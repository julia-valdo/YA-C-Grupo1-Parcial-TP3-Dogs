package team.ya.c.grupo1.dogit.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularimageview.CircularImageView
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.databinding.ActivityMainBinding
import team.ya.c.grupo1.dogit.entities.ThemeProviderEntity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //TODO: Ver manejo de errores
        RxJavaPlugins.setErrorHandler { throwable: Throwable? -> }

        setupVariables()
        setupBottomNavMenu()
        setupNavigationView()

        val theme = ThemeProviderEntity(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    private fun  setupVariables() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMainActivity) as NavHostFragment

        this.navController = navHostFragment.navController
        this.appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayoutMainActivity)

        binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)
    }

    private fun setupBottomNavMenu() {
        NavigationUI.setupWithNavController(binding.bottomNavigationViewMainActivity, navController)
    }

    private fun setupNavigationView(){
        setupNavigationViewHeaderVariables()
        binding.navViewMainActivity.setupWithNavController(navController)
        binding.toolbarMainActivity.setupWithNavController(navController, appBarConfiguration)

        val drawerFragmentIds = mutableSetOf<Int>()
        for (i in 0 until binding.navViewMainActivity.menu.size()) {
            val item = binding.navViewMainActivity.menu.getItem(i)
            drawerFragmentIds.add(item.itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.txtMainActivityToolbarTitle.text = navController.currentDestination?.label

            if (destination.id in drawerFragmentIds || destination.id == R.id.detailsFragment) {
                binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_back)
            } else {
                binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)

                if (binding.drawerLayoutMainActivity.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START)
                }
            }
        }

        binding.toolbarMainActivity.setNavigationOnClickListener {
            when {
                binding.drawerLayoutMainActivity.isDrawerOpen(GravityCompat.START) -> {
                    binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START)
                    binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)
                }

                navController.currentDestination?.id in drawerFragmentIds -> {
                    navController.navigateUp()
                }

                navController.currentDestination?.id == R.id.detailsFragment -> {
                    navController.navigateUp()
                }

                else -> {
                    binding.drawerLayoutMainActivity.openDrawer(GravityCompat.START)
                    binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_back)
                }
            }
        }

        binding.drawerLayoutMainActivity.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                if (navController.currentDestination?.id !in drawerFragmentIds) {
                    binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        overrideBackButton()
    }

    private fun setupNavigationViewHeaderVariables(){
        val userEmail = FirebaseAuth.getInstance().currentUser?.email?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userEmail)
            .get()
            .addOnSuccessListener {
                safeActivityCall {
                    val name = "${it.getString("firstName")} ${it.getString("surname")}"
                    val profileImage = it.getString("profileImage")

                    val headerView = binding.navViewMainActivity.getHeaderView(0)
                    val txtUserName = headerView.findViewById<TextView>(R.id.txtDrawerMainNavHeaderUserName)
                    txtUserName.text = name

                    val imgProfileImage = headerView.findViewById<CircularImageView>(R.id.imgDrawerMainNavHeaderProfilePicture)

                    if (profileImage == "") {
                        imgProfileImage.setImageResource(R.drawable.img_avatar)
                        return@safeActivityCall
                    }

                    Glide.with(this)
                        .load(profileImage)
                        .placeholder(R.drawable.img_avatar)
                        .error(R.drawable.img_avatar)
                        .into(imgProfileImage)
                }
            }
    }

    private fun safeActivityCall(action: () -> Unit) {
        if (!isFinishing && !isDestroyed) {
            action()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_nav_menu, menu)
        return true
    }

    fun hideBottomNavMenu() {
        binding.bottomNavigationViewMainActivity.visibility = View.GONE
    }

    fun showBottomNavMenu() {
        binding.bottomNavigationViewMainActivity.visibility = View.VISIBLE
    }

    private fun overrideBackButton() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                binding.drawerLayoutMainActivity.isDrawerOpen(GravityCompat.START) -> {
                    binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START)
                    binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)
                }

                navController.currentDestination?.id != R.id.homeFragment -> {
                    navController.navigateUp()
                }

                else -> {
                    finish()
                }
            }
        }
    }
}