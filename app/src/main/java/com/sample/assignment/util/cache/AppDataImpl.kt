package com.sample.assignment.util.cache

import android.content.Context
import com.sample.assignment.util.security.SecuredPreferenceStorage
import com.sample.core.data.cache.AppData
import com.sample.core.di.qualifier.ApplicationContext
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.imagecompressor.AppImageCompressor
import com.sample.core.utility.imagecompressor.AppImageCompressor.Companion.FILE_PREFIX
import com.sample.core.utility.imagecompressor.AppImageCompressor.Companion.FILE_SUFFIX
import com.sample.core.utility.security.Base32String
import javax.inject.Inject

class AppDataImpl @Inject constructor(
    private val securedPreferenceStorage: SecuredPreferenceStorage,
    @ApplicationContext private val context: Context
) : AppData {


    /**
     * Since [SecuredPreferenceStorage] will encrypt this value with AES.
     * we can further add one more layer to this..i.e encoding the value to [Base32String]
     * prior to AES
     *
     */
    override fun saveUserID(userId: String) {
        securedPreferenceStorage.encryptKeyValueSet(
            SecuredPreferenceStorage.SECURE_ENCRYPTED_USER_ID,
            Base32String.encode(userId.toByteArray())
        )
    }

    override fun saveEmail(email: String) {
        securedPreferenceStorage.encryptKeyValueSet(
            SecuredPreferenceStorage.SECURE_ENCRYPTED_EMAIL,
            Base32String.encode(email.toByteArray())
        )
    }

    override fun savePassword(password: String) {
        securedPreferenceStorage.encryptKeyValueSet(
            SecuredPreferenceStorage.SECURE_ENCRYPTED_PASSWORD,
            Base32String.encode(password.toByteArray())
        )
    }

    override fun getUserId(): String {
        return Base32String.decode(
            securedPreferenceStorage.decryptValue(SecuredPreferenceStorage.SECURE_ENCRYPTED_USER_ID)
                .safeGet()
        ).decodeToString()
    }

    override fun getEmail(): String {
        return Base32String.decode(
            securedPreferenceStorage.decryptValue(SecuredPreferenceStorage.SECURE_ENCRYPTED_EMAIL)
                .safeGet()
        ).decodeToString()
    }

    override fun getPassword(): String {
        return Base32String.decode(
            securedPreferenceStorage.decryptValue(SecuredPreferenceStorage.SECURE_ENCRYPTED_PASSWORD)
                .safeGet()
        ).decodeToString()
    }

    override fun clearStorage() {
        securedPreferenceStorage.removeKeyValueSet(SecuredPreferenceStorage.SECURE_ENCRYPTED_USER_ID)
        securedPreferenceStorage.removeKeyValueSet(SecuredPreferenceStorage.SECURE_ENCRYPTED_EMAIL)
        securedPreferenceStorage.removeKeyValueSet(SecuredPreferenceStorage.SECURE_ENCRYPTED_PASSWORD)
        securedPreferenceStorage.removeKeyValueSet(SecuredPreferenceStorage.SECURE_ENCRYPTED_FILE_PATH)
        clearCacheDir()
    }


    /**
     * clears CAMERA cache and cache images for glide as well.
     */
    override fun clearCacheDir() {
        context.cacheDir.deleteRecursively()
    }


    /**
     *clears only Camera cache.
     */
    override fun clearCameraCacheImageOnly() {
        context.cacheDir.listFiles()?.filter {
            it.name.equals(FILE_PREFIX + getUserId() + FILE_SUFFIX)
        }.run {
            if (this.isNullOrEmpty().not()) {
                this?.get(0)?.deleteRecursively()
            }
        }
    }

    override fun removeGalleryPath() {
        securedPreferenceStorage.removeKeyValueSet(SecuredPreferenceStorage.SECURE_ENCRYPTED_FILE_PATH)
    }


    override fun saveGalleryPhotSelectedPath(filePath: String) {

        clearCacheDir()

        securedPreferenceStorage.encryptKeyValueSet(
            SecuredPreferenceStorage.SECURE_ENCRYPTED_FILE_PATH,
            Base32String.encode(filePath.toByteArray())
        )
    }

    override fun getGalleryPhotoFilePath(): String {
        return Base32String.decode(
            securedPreferenceStorage.decryptValue(SecuredPreferenceStorage.SECURE_ENCRYPTED_FILE_PATH)
                .safeGet()
        ).decodeToString()
    }

    override var hasUserID =
        securedPreferenceStorage.hasKey(SecuredPreferenceStorage.SECURE_ENCRYPTED_USER_ID).safeGet()

}