package com.sample.core.data.cache

interface AppData{
    fun saveUserID(userId : String)

    fun saveEmail(email : String)

    fun savePassword(password : String)

    fun getUserId() : String

    fun getEmail() : String

    fun getPassword() : String

    fun clearStorage()

    fun clearCacheDir()

    fun saveGalleryPhotSelectedPath(filePath : String)

    fun getGalleryPhotoFilePath() : String

    fun clearCameraCacheImageOnly()

    fun removeGalleryPath()

    var hasUserID : Boolean
}
