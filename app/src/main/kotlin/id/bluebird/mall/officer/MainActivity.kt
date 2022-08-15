package id.bluebird.mall.officer

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import id.bluebird.mall.officer.databinding.ActivityMainBinding
import id.bluebird.mall.officer.extensions.backArrowButton
import id.bluebird.mall.officer.extensions.setToolbarAddFleetFragment
import id.bluebird.mall.officer.extensions.setToolbarCreateUserFragment
import id.bluebird.mall.officer.logout.LogoutDialog

internal class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        navController.addOnDestinationChangedListener { _, destination, args ->
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            with(mBinding) {
                when (destination.id) {
                    R.id.loginFragment, R.id.splashFragment -> {
                        toolbar.visibility = View.GONE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                    R.id.createUserFragment -> {
                        navigateToCreateUser(args = args)
                    }
                    R.id.addFleetFragment -> {
                        navigateBackWithArrow(R.id.addFleetFragment)
                    }
                    R.id.searchFleetFragment -> {
                        navigateBackWithArrow(R.id.searchFleetFragment)
                    }
                    R.id.searchQueueFragment -> {
                        navigateBackWithArrow(R.id.searchQueueFragment)
                    }
                    R.id.monitoringFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                        toolbarVisibility()
                    }
                    R.id.searchLocationFragment -> {
                        navigateBackWithArrow(R.id.searchLocationFragment)
                    }
                    else -> {
                      toolbarVisibility()
                    }
                }
            }
        }

        mBinding.navView.setNavigationItemSelectedListener(this)
    }

    private fun toolbarVisibility(){
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
                    R.id.queueFleetFragment,
                    R.id.queuePassengerFragment,
                    R.id.monitoring_nav,
                    R.id.userListFragment
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

    private fun navigateToCreateUser(args: Bundle?) {
        with(mBinding) {
            hideDrawerMenu()
            toolbar.setToolbarCreateUserFragment(
                actionBar = supportActionBar,
                args = args
            )
            toolbar.backArrowButton(
                navController,
                destinationId = R.id.createUserFragment
            )
        }
    }

    private fun navigateBackWithArrow(id: Int) {
        with(mBinding) {
            hideDrawerMenu()
            toolbar.setToolbarAddFleetFragment(
                actionBar = supportActionBar
            )
            toolbar.backArrowButton(
                navController,
                destinationId = id
            )
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> {
                drawer()
                LogoutDialog().show(supportFragmentManager, LogoutDialog.TAG)
            } else -> {
                NavigationUI.onNavDestinationSelected(item, navController)
                drawer()
            }
        }
        return true
    }
}