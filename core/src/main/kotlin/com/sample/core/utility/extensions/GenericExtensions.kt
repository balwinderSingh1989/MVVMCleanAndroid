package com.sample.core.utility.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import com.sample.core.BuildConfig

/**
 * Returns false if 'null', else value
 */
fun Boolean?.safeGet(): Boolean = this ?: false

/**
 * Launch 3rd party intent with custom [action] (if specified) & [uriString]
 */
fun Context.launchIntent(action: String = Intent.ACTION_VIEW, uriString: String) =
    startActivity(Intent(action, Uri.parse(uriString)))

fun isProdFlavor() = BuildConfig.FLAVOR_environment == "live"


val String.Companion.empty: String get() = String()

/**
 * Returns empty string if 'null', else value
 */
fun String?.safeGet(): String = this ?: String.empty


/**
 * Kotlin extension to get the class name.
 *
 * This can also be used a TAGs for logging.
 *
 * @return Caller class' name
 */
val Any.TAG: String get() = javaClass.simpleName


/**
 * Return true if string is validEmail
 */
fun String?.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(
    this
).matches()

