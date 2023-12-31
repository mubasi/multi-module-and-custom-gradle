package id.multi.module.custome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import id.multi.module.custome.core.utils.hawk.UserUtils
import id.multi.module.custome.extensions.backArrowButton
import id.multi.module.custome.extensions.setToolbarBackArrow
import id.multi.module.custome.extensions.setToolbarCreateUserFragment
import id.multi.module.custome.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        fun startNewIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            return intent
        }
    }

    private lateinit var mNavController: androidx.navigation.NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var mToogle: ActionBarDrawerToggle
    private lateinit var mBinding: ActivityMainBinding

    private val navController by lazy {
        navHostFragment.navController
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        setupNav()
        drawer()
        setToggle()
        setNavViewByUserRole()
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(mBinding.navView, navController)
        navController.setGraph(R.navigation.main_nav)
        navController()

        mBinding.navView.setNavigationItemSelectedListener(this)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun navController() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            with(mBinding) {
                when (destination.id) {
                    R.id.homepageFragment -> {
                        toolbar.visibility = View.GONE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        navigateBackWithArrow(destination.id)
                    }
                    R.id.profilePageFragment -> {
                        toolbarVisibility()
                        navigateBackWithArrow(destination.id)
                    }
                    else -> {
                        toolbarVisibility()
                    }
                }
            }
        }
    }

    private fun toolbarVisibility() {
        with(mBinding) {
            toolbar.visibility = View.VISIBLE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            setToggle()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun drawer() {
//        mBinding.tvVersion.text = BuildConfig.VERSION_NAME
        if (mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setNavViewByUserRole() {
        navController.setGraph(R.navigation.main_nav)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.profilePageFragment,
                    R.id.homepageFragment
                ), mBinding.drawerLayout
            )
    }


    private fun setupNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        mNavController = navHostFragment.navController
    }

    private fun setToggle() {
        mToogle = ActionBarDrawerToggle(
            this,
            mBinding.drawerLayout,
            mBinding.toolbar,
            R.string.nav_app_bar_open_drawer_description,
            R.string.nav_app_bar_navigate_up_description
        )
        mBinding.drawerLayout.addDrawerListener(mToogle)
        mToogle.isDrawerIndicatorEnabled = true
        mToogle.syncState()
    }

    private fun hideDrawerMenu() {
        mToogle.isDrawerIndicatorEnabled = false
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun navigateBackWithArrow(id: Int) {
        with(mBinding) {
            hideDrawerMenu()
            toolbar.setToolbarBackArrow(
                actionBar = supportActionBar
            )
            toolbar.backArrowButton(
                navController,
                destinationId = id
            )
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> {
                NavigationUI.onNavDestinationSelected(item, navController)
                drawer()
            }
        }
        return true
    }
}