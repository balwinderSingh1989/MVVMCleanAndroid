package com.sample.assignment.util.extension

import android.app.Activity
import android.content.Intent
import com.sample.assignment.ui.main.MainActivity
import com.sample.assignment.util.DialogUtils
import com.sample.assignment.util.dialogs.AlertDialogListener
import com.sample.core.domain.remote.exception.PermissionException


/**
 * Relaunch the specified activity [classToLaunch] freshly with option of animating the activity
 * transition.
 *
 * [NOTE] This will clear the activity back-stack & navigate to the [classToLaunch] activity
 */
fun <T> Activity.relaunchApp(classToLaunch: Class<T>, animate: Boolean = false) {
    startActivity(Intent(this, classToLaunch))
    if (animate.not()) {
        overridePendingTransition(0, 0)
    }
    finishAffinity()
}

/**
 * Relaunch the app freshly with option of animating the activity transition
 *
 * [NOTE] This will clear the activity back-stack & navigate to the [DeepLinkingActivity] activity
 */
fun Activity.relaunchApp(animate: Boolean = false) {
    relaunchApp(
        classToLaunch = MainActivity::class.java,
        animate = animate
    )

    if (animate.not()) {
        overridePendingTransition(0, 0)
    }

    finishAffinity()
}

/**
 * Launch permission form setting screen.
 * this will launch a dialog with  required permission [message] and button [positiveButtonText]
 * to navigate to settings
 *
 */
fun Activity.showPermissionFromSettingsDialog(
    message: String,
    positiveButtonText: String,
    permissionException: PermissionException
) {
    this.let {
        DialogUtils.twoButtonDialog(
            context = it,
            message = message,
            alertDialogListener = object : AlertDialogListener {
                override fun onPositive() {
                    permissionException.goToSettings()
                }

                override fun onNegative() {}
            },
            positiveButtonText = positiveButtonText,
            cancelable = true
        )
    }
}