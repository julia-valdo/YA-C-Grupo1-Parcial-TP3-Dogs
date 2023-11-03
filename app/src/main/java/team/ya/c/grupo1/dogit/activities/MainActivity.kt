package team.ya.c.grupo1.dogit.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
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
        binding.navViewMainActivity.setupWithNavController(navController)
        binding.toolbarMainActivity.setupWithNavController(navController, appBarConfiguration)

        val drawerFragmentIds = mutableSetOf<Int>()
        for (i in 0 until binding.navViewMainActivity.menu.size()) {
            val item = binding.navViewMainActivity.menu.getItem(i)
            drawerFragmentIds.add(item.itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.txtMainActivityToolbarTitle.text = navController.currentDestination?.label
            if (destination.id in drawerFragmentIds) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_nav_menu, menu)
        return true
    }

    fun hideBottomNavMenu() {
        binding.bottomNavigationViewMainActivity.visibility = android.view.View.GONE
    }

    fun showBottomNavMenu() {
        binding.bottomNavigationViewMainActivity.visibility = android.view.View.VISIBLE
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