package com.sample.core.data.repository.gavatar.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sample.core.domain.remote.BaseResponse

sealed class GavatarResponse {

    data class Entry(
        @Expose @SerializedName("id") val id: String,
        @Expose @SerializedName("hash") val hash: String,
        @Expose @SerializedName("requestHash") val requestHash: String,
        @Expose @SerializedName("profileUrl") val profileURL: String,
        @Expose @SerializedName("preferredUsername") val preferredUsername: String,
        @Expose @SerializedName("thumbnailUrl") val thumbnailURL: String,
        @Expose @SerializedName("photos") val photos: List<Photo>,
        @Expose @SerializedName("name") val name: List<Any?>,
        @Expose @SerializedName("displayName") val displayName: String,
        @Expose @SerializedName("urls") val urls: List<Any?>
    )

    data class Photo(
        @Expose @SerializedName("value") val value: String,
        @Expose @SerializedName("type") val type: String
    )

    data class GavatarEntry(
        @Expose @SerializedName("entry") val entry: List<Entry>? = null
    ) : BaseResponse()
}