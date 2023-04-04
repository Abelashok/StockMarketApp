package tel.testing

import com.google.gson.annotations.SerializedName

data class HeaderModel(
    @SerializedName("deviceID")
    val deviceID: String,

    @SerializedName("tid")
    val tid: String?,

    @SerializedName("feature_id")
    val feature_id: String?,

    @SerializedName("version")
    val version: String,

    @SerializedName("timestamp")
    val timestamp: String
)