package tel.testing

import com.google.gson.annotations.SerializedName

data class PayloadModel(
    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("latitude")
    val latitude: Double?,
)