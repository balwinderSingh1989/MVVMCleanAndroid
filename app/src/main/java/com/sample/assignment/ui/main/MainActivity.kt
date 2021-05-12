package com.sample.assignment.ui.main

import androidx.navigation.Navigation.findNavController
import com.sample.assignment.BR
import com.sample.assignment.R
import com.sample.assignment.base.BaseActivity
import com.sample.assignment.databinding.ActivityMainBinding


class MainActivity
    : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    override val viewModel = MainActivityViewModel::class.java
    override val layoutId = R.layout.activity_main
    override fun getBindingVariable() = BR.viewModel


    override fun initUserInterface() {
        launchSreen()
    }

    private fun launchSreen() {
        val navController = findNavController(this, R.id.act_home_host_fragment)
        val graph = navController.navInflater.inflate(R.navigation.main_navigation)
        if (injectedViewModel.isUserLoggedIn) {
            //if admin user\
            graph.startDestination = R.id.profilr_fragment

        } else {
            graph.startDestination = R.id.login_fragment
        }
        navController.graph = graph
    }

    override fun onBackPressed() {
    }


}
