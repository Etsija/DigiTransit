package com.etsija.digitransit.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.R
import com.etsija.digitransit.repository.SharedRepository
import com.etsija.digitransit.viewmodel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // ViewModel for this activity's lifecycle
    val viewModel: SharedViewModel by lazy {
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise the SharedViewModel
        viewModel.init()

        // Controller for navigating between components (fragments)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level fragments
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.alertsFragment,
            R.id.stopsFragment
        ))

        // Setup top app bar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup bottom navigation bar
        setupWithNavController(
            findViewById<BottomNavigationView>(R.id.bottomNavigation),
            navHostFragment.navController
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}