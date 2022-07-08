package id.bluebird.mall.officer

import android.os.Bundle
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
import id.bluebird.mall.feature_user_management.utils.NavControllerUserDomain
import id.bluebird.mall.officer.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity() {

    private lateinit var mNavController: androidx.navigation.NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var mToogle: ActionBarDrawerToggle
    private lateinit var mBinding: ActivityMainBinding

    private val navController by lazy {
        navHostFragment.navController
    }

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
//        mBinding.navView.menu.findItem(R.id.action_perimeter).isVisible = false

        navController.addOnDestinationChangedListener { nav, destination, args ->
            with(mBinding) {
                when (destination.id) {
                    R.id.loginFragment, R.id.splashFragment -> {
                        toolbar.visibility = View.GONE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                    R.id.createUserFragment -> {
                        mToogle.isDrawerIndicatorEnabled = false
                        NavControllerUserDomain.setToolbar(supportActionBar, toolbar, nav, args)
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    }
                    else -> {
                        toolbar.visibility = View.VISIBLE
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        setToggle()
                    }
                }
            }
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

    private fun setNavViewByUserRole() {
        navController.setGraph(R.navigation.main_nav)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.queueFleetFragment,
                    R.id.queuePassengerFragment,
                    R.id.action_monitoring,
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
}