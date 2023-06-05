package id.bluebird.vsm.pangkalan

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
import id.bluebird.vsm.core.BuildConfig
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.feature.select_location.SelectNavigationVariable
import id.bluebird.vsm.pangkalan.databinding.ActivityMainBinding
import id.bluebird.vsm.pangkalan.extensions.backArrowButton
import id.bluebird.vsm.pangkalan.extensions.setToolbarBackArrow
import id.bluebird.vsm.pangkalan.extensions.setToolbarCreateUserFragment
import id.bluebird.vsm.pangkalan.logout.FragmentLogoutDialog

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
        navController.addOnDestinationChangedListener { _, destination, args ->
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mBinding.navView.menu.findItem(R.id.user_management_nav).isVisible =
                UserUtils.isUserOfficer().not()

            setupVisibleToolbar(destination.id)

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
                    R.id.addByCameraFragment -> {
                        navigateBackWithArrow(R.id.addByCameraFragment)
                    }
                    R.id.searchFleetFragment -> {
                        navigateBackWithArrow(R.id.searchFleetFragment)
                    }
                    R.id.qrCodeFragment -> {
                        navigateBackWithArrow(R.id.qrCodeFragment)
                    }
                    R.id.searchQueueFragment -> {
                        navigateBackWithArrow(R.id.searchQueueFragment)
                    }
                    R.id.queueFleetFragment -> {
                        setQueueToolbar(R.id.queueFleetFragment)
                    }
                    R.id.queuePassengerFragment -> {
                        setQueueToolbar(R.id.queuePassengerFragment)
                    }
                    R.id.queueTicket -> {
                        navigateBackWithArrow(R.id.queueTicket)
                    }
                    R.id.monitoringFragment -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                    }
                    R.id.searchLocationFragment -> {
                        navigateBackWithArrow(R.id.searchLocationFragment)
                    }
                    R.id.selectLocationFragment -> {
                        setToolbarTittleForLocationFragment(args)
                    }
                    R.id.monitoringFragmentSearch -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                        navigateBackWithArrow(R.id.monitoringFragmentSearch)
                    }
                    R.id.ritaseFleetFragment  -> {
                        navigateBackWithArrow(R.id.ritaseFleetFragment)
                    }
                    R.id.queueCarFleetFragment -> {
                        setQueueToolbar(R.id.queueCarFleetFragment)
                    }
                    R.id.addCarFleetFragment -> {
                        navigateBackWithArrow(R.id.addCarFleetFragment)
                    }
                    R.id.carFleetAddByCamera -> {
                        navigateBackWithArrow(R.id.carFleetAddByCamera)
                    }
                    R.id.searchCarFleetFragment -> {
                        navigateBackWithArrow(R.id.searchCarFleetFragment)
                    }
                    else -> {
                        toolbarVisibility()
                    }
                }
            }
        }
    }

    private fun setQueueToolbar(id:Int){
        if (UserUtils.isUserOfficer().not()) {
            navigateBackWithArrow(id = id)
        } else {
            toolbarVisibility()
        }
    }

    private fun setToolbarTittleForLocationFragment(args: Bundle?) {
        val isMenu = args?.getBoolean(SelectNavigationVariable.IS_MENU)
        mBinding.toolbar.title =
            if (isMenu == true) getString(R.string.fleet_title) else getString(R.string.queue_passenger)
        toolbarVisibility()
    }

    private fun toolbarVisibility() {
        with(mBinding) {
            toolbar.visibility = View.VISIBLE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            setToggle()
        }
    }

    private fun setupVisibleToolbar(destination : Int){
        if(destination == R.id.monitoringFragmentSearch) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
            toolbarVisibility()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun drawer() {
        mBinding.tvVersion.text = BuildConfig.VERSION_NAME
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
                    R.id.searchLocationFragment,
                    R.id.queuePassengerFragment,
                    R.id.queueCarFleetFragment,
                    R.id.monitoring_nav,
                    R.id.user_management_nav
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
            R.id.action_logout -> {
                drawer()
                FragmentLogoutDialog().show(supportFragmentManager, FragmentLogoutDialog.TAG)
            }
            else -> {
                NavigationUI.onNavDestinationSelected(item, navController)
                drawer()
            }
        }
        return true
    }
}