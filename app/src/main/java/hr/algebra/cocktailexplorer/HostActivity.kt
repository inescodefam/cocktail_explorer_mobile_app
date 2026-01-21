package hr.algebra.cocktailexplorer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import hr.algebra.PreferenceActivity
import hr.algebra.cocktailexplorer.databinding.ActivityHostBinding
import hr.algebra.cocktailexplorer.notification.NotificationHelper

class HostActivity : BaseActivity() {
    private lateinit var binding: ActivityHostBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var notificationHelper: NotificationHelper


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.notification_permission_granted), Toast.LENGTH_SHORT).show()
            notificationHelper.sendTestNotification()
        } else {
            Toast.makeText(this, getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleTransition()
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        setupBottomNavigation()
        setupNavigationDrawer()
    }

    override fun onResume() {
        super.onResume()

        Toast.makeText(
            this,
            preferences.getString("savedPreferences", ""),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navController) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuCocktails -> {
                    navController.navigate(R.id.menuCocktails)
                    true
                }
                R.id.menuFavorites -> {
                    navController.navigate(R.id.menuFavorites)
                    true
                }
                R.id.menuItems -> {
                    toggleDrawer()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)

//        // tražilica
//        val searchItem = menu?.findItem(R.id.action_search)
//        val searchView = searchItem?.actionView as SearchView
//
//        searchView.queryHint = getString(R.string.search_hint)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                sendSearchQuery(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                sendSearchQuery(newText)
//                return true
//            }
//
//        })

        return true
    }

//    private fun sendSearchQuery(query: String?) {
//        supportFragmentManager.setFragmentResult(
//            "searchQuery",
//            Bundle().apply { putString("query", query) }
//        )
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miPref -> {
                showPreferences()
                return true
            }
            R.id.miExit -> {
                exitApp()
                return true
            }
            android.R.id.home -> {
                toggleDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuAbout -> {
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.navController) as NavHostFragment
                    navHostFragment.navController.navigate(R.id.menuAbout)
                }
                R.id.miPref -> {
                    showPreferences()
                }
                R.id.miNotifications -> {
                    handleNotificationMenuClick()
                }
                R.id.miExit -> {
                    exitApp()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }


    private fun handleNotificationMenuClick() {
        when {
            notificationHelper.hasNotificationPermission() -> {
                showNotificationOptionsDialog()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionRationaleDialog()
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            else -> {
                showNotificationOptionsDialog()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.notification_permission_title)
            setMessage(R.string.notification_permission_rationale)
            setPositiveButton(R.string.grant_permission) { _, _ ->
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }


    private fun showNotificationOptionsDialog() {
        val options = arrayOf(
            getString(R.string.send_test_notification),
            getString(R.string.schedule_reminder),
            getString(R.string.check_network_status)
        )

        AlertDialog.Builder(this).apply {
            setTitle(R.string.notification_options)
            setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        notificationHelper.sendTestNotification()
                        Toast.makeText(
                            this@HostActivity,
                            getString(R.string.notification_sent),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> {
                        notificationHelper.scheduleReminderNotification(
                            delayMillis = 10_000,
                            cocktailName = "your favorite cocktail",
                            cocktailId = 0
                        )
                        Toast.makeText(
                            this@HostActivity,
                            getString(R.string.reminder_scheduled),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    2 -> {
                        val isOnline = notificationHelper.isNetworkAvailable()
                        val networkType = notificationHelper.getNetworkType()
                        val message = if (isOnline) {
                            getString(R.string.network_connected, networkType)
                        } else {
                            getString(R.string.network_disconnected)
                        }
                        Toast.makeText(this@HostActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun showPreferences() {
        startActivity(Intent(this, PreferenceActivity::class.java))
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setMessage(getString(R.string.do_you_really_want_to_exit_an_app))
            setIcon(R.drawable.close)
            setCancelable(true)
            setNegativeButton(getString(R.string.close), null)
            setPositiveButton("OK") { _, _ -> finish() }
            show()
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}