package com.sample.assignment.ui.profile

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.assignment.base.BaseViewModel
import com.sample.assignment.util.security.AdvancedSecurityUtil
import com.sample.core.data.cache.AppData
import com.sample.core.data.repository.login.model.LoginRequest
import com.sample.core.domain.kotlinflow.Resource
import com.sample.core.domain.usecase.GavatarUseCase
import com.sample.core.domain.usecase.LoginUseCase
import com.sample.core.utility.event.Event
import com.sample.core.utility.event.SingleLiveEvent
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.imagecompressor.AppImageCompressor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class ProfileViewModel @Inject constructor(
    private var appImageCompressor: AppImageCompressor,
    private val appData: AppData,
    private val gavatarUseCase: GavatarUseCase
) : BaseViewModel() {

    var userName = ObservableField<String>()
    var userEmail = ObservableField<String>()

    var userPassword = ObservableField<String>()

    var userProfileURL = ObservableField<String>()

    var userImageFile = ObservableField<File?>()

    val allowedMimeType = arrayListOf(
        "image/png", "image/jpg", "image/jpeg"
    )
    private var _attachmentClicked = SingleLiveEvent<Void>()
    val attachmentClickEvent: LiveData<Void>
        get() = _attachmentClicked


    private var _logoutClicked = SingleLiveEvent<Void>()
    val logoutEvent: LiveData<Void>
        get() = _logoutClicked

    private val _gavatarAPIfailureEvent = MutableLiveData<Event<String>>()
    val gavatarAPIfailureEvent: LiveData<Event<String>>
        get() = _gavatarAPIfailureEvent

    fun popualteUI() {
        userName.set(appData.getUserId())
        userEmail.set(appData.getEmail())
        userPassword.set(appData.getPassword())
        getImageFromCache()
    }

    /**
     * Try to get image from cache in order
     * 1. CAMERA  (from cacheDir)
     * 2  GALLERY (retrive file path from secureStorage)
     * 3. REMOTE : construct URL from hashed email and pass the same to glide.
     *    Glide would load if present in cache
     */
    private fun getImageFromCache() {


        appImageCompressor.getSavedAvatar(userName.get().safeGet())?.let {
            userImageFile.set(it)
        } ?: let {
            //else try to fetch from saved gallery path
            appData.getGalleryPhotoFilePath().let {
                if (it.isEmpty().not()) {
                    userImageFile.set(File(it))
                } else {
                    //try loading from glide now
                    //  val md5hash = AdvancedSecurityUtil.md5(appData.getEmail().safeGet())
                    val md5hash = "205e460b479e2e5b48aec07710c08d50"
                    userProfileURL.set("https://en.gravatar.com/avatar/$md5hash")
                }
            }
        }
    }

    fun onCameraClick() {
        _attachmentClicked.call()
    }

    suspend fun compressCameraImage(imageBitmap: Bitmap): File {
        return appImageCompressor.getCompressedJpeg(imageBitmap, userId = userName.get().safeGet())
    }


    fun cacheGallerySelectedPhotoPath(filePath: String) {
        appData.saveGalleryPhotSelectedPath(filePath)
    }

    fun loadFromGavatar() {
        userEmail.get().safeGet().run {
            if (this.isNotEmpty()) {
                val md5hash = AdvancedSecurityUtil.md5(this)
                if (md5hash.isNotEmpty())

                    viewModelScope.launch {

                        showLoading("Fetching avatar..")
                        gavatarUseCase.execute(
                            GavatarUseCase.Params.create(
                                email = "205e460b479e2e5b48aec07710c08d50"
                            )
                        ).collect { loadResult ->
                            when (loadResult) {
                                is Resource.Success -> {

                                    hideLoading()
                                    loadResult.data.entry?.let {
                                        if (it.isEmpty().not()) {

                                            appData.removeGalleryPath()
                                            appData.clearCameraCacheImageOnly()

                                            userProfileURL.set(it[0].thumbnailURL)
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    val md5hash = "205e460b479e2e5b48aec07710c08d50"
                                    userProfileURL.set("https://en.gravatar.com/avatar/$md5hash")
                                    _gavatarAPIfailureEvent.postValue(Event("Failed to get avatar with error :${loadResult.errorDescription}"))
                                    hideLoading()

                                }
                            }
                        }

                    }


            }

        }

    }

    fun logout() {
        clearSession()
        appData.clearStorage()
        _logoutClicked.call()
    }


}