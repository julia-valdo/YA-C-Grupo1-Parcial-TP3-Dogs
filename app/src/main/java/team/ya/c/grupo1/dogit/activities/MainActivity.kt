package team.ya.c.grupo1.dogit.activities

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.databinding.ActivityMainBinding


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

        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.txtMainActivityToolbarTitle.text = navController.currentDestination?.label
            binding.toolbarMainActivity.setNavigationIcon(R.drawable.icon_hambuger_menu)
        }

        binding.toolbarMainActivity.setNavigationOnClickListener {
            binding.drawerLayoutMainActivity.openDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_nav_menu, menu)
        return true
    }
}