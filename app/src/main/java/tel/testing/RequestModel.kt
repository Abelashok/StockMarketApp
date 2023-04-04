package tel.testing

import com.google.gson.annotations.SerializedName

data class RequestModel(
    val header: HeaderModel,
    val payload: Payload
)

data class Payload(
    @SerializedName("request_time")
    val request_time: String?,

    @SerializedName("feature_data")
    val feature_data: String?,

    @SerializedName("feature_data1")
    val feature_data1: List<PayloadModel>?,

    @SerializedName("feature_data2")
    val feature_data2: List<PayloadModel>?,
)

