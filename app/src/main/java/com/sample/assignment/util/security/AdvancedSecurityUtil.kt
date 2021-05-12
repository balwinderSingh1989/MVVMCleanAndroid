package com.sample.assignment.util.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sample.assignment.BuildConfig
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.extensions.empty
import com.sample.core.utility.logger.AppLogger
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.MessageDigest

/**
 * This class provides all the functions for creating advanced security options
 * like Jetpack security crypto, Encrypted Shared Preference, AES Encryption, Decryption logic...
 */
object AdvancedSecurityUtil {
    private const val MASTER_ALIAS = "MASTER." + BuildConfig.APPLICATION_ID
    private const val secured_shared_preference_name = "secured_data"

    /**
     * Note, this method can create multiple instances of master alias,
     * please use it wisely as per the requirement
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createAdvancedMasterKeys(
        context: Context,
        masterAlias: String = MASTER_ALIAS
    ): MasterKey? {
        AppLogger.d(TAG, "Creating AdvancedMasterKeys with alias => $masterAlias")
        val advancedSpec = KeyGenParameterSpec.Builder(
            masterAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setUnlockedDeviceRequired(true)
                //Using secure element
                setIsStrongBoxBacked(true)
            }
        }.build()

        return MasterKey.Builder(context, MASTER_ALIAS)
            .setKeyGenParameterSpec(advancedSpec).build()
    }


    private fun createSecuredSharedPrefs(
        context: Context,
        masterKey: MasterKey,
        preference_file_name: String = secured_shared_preference_name
    ): SharedPreferences? {
        var securedSharedPrefs: SharedPreferences? = null
        AppLogger.d(TAG, "Creating createSecuredSharedPrefs with filename - $preference_file_name")
        try {
            securedSharedPrefs = EncryptedSharedPreferences.create(
                context,
                preference_file_name,   //xml file name
                masterKey,   //master key
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,  //key encryption technique
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM //value encryption technique
            )
        } catch (e: GeneralSecurityException) {
            AppLogger.e(TAG, "Exception creating encrypted SharedPreference")
        }
        return securedSharedPrefs
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun getSecuredSharedPref(
        context: Context
    ): SharedPreferences? {
        val masterKey = createAdvancedMasterKeys(context)
        return masterKey?.let {
            createSecuredSharedPrefs(context, it)
        }
    }

    /**
     * www.gravatar.com required string to be in lowercase first
     */
    fun md5(input:String = String.empty): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.trim().toLowerCase().toByteArray())).toString(16).padStart(32, '0')
    }
}