package com.sample.assignment.util.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.sample.core.di.qualifier.ApplicationContext
import com.sample.core.di.scope.PerApplication
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.imagecompressor.AppImageCompressor.Companion.FILE_PREFIX
import com.sample.core.utility.logger.AppLogger
import javax.inject.Inject

/***
 * This class will expose functions, for saving data to encrypted shared preferences
 * hiding the underlying details of how the data is encrypted & stored securely
 */
@PerApplication
@RequiresApi(Build.VERSION_CODES.M)
class SecuredPreferenceStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var securedSharedPrefs: SharedPreferences? = null

    companion object {
        const val SECURE_ENCRYPTED_USER_ID = "SECURE_ENCRYPTED_USER_ID"
        const val SECURE_ENCRYPTED_EMAIL = "SECURE_ENCRYPTED_EMAIL"
        const val SECURE_ENCRYPTED_PASSWORD = "SECURE_ENCRYPTED_PASSWORD"
        const val SECURE_ENCRYPTED_FILE_PATH = "SECURE_ENCRYPTED_FILE_PATH"
    }


    init {
        securedSharedPrefs = AdvancedSecurityUtil.getSecuredSharedPref(context)
    }

    /**
     * private functions which handle shared preference encryption, decryption, saving, deleting
     */

    fun encryptKeyValueSet(key: String, value: String) {
        securedSharedPrefs?.edit()?.putString(key, value)?.apply()
    }

    fun encryptKeyValueSet(key: String, value: Int) {
        securedSharedPrefs?.edit()?.putInt(key, value)?.apply()
    }

    fun decryptValue(key: String): String? {
        return securedSharedPrefs?.getString(key, null)
    }

    fun decryptIntValue(key: String): Int? {
        return securedSharedPrefs?.getInt(key, 0)
    }


    fun removeKeyValueSet(key: String) {
        securedSharedPrefs?.edit()?.remove(key)?.apply()
    }

    fun hasKey(key: String): Boolean {
        return securedSharedPrefs?.contains(key).safeGet()
    }



}