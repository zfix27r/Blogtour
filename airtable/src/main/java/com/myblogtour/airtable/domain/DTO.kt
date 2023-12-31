package com.myblogtour.airtable.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PublicationDTO(
    val records: List<RecordPublication>,
) : Parcelable

@Parcelize
data class RecordPublication(
    val id: String,
    val createdTime: String,
    val fields: FieldsPublication,
) : Parcelable

@Parcelize
data class FieldsPublication(
    val id: Long,
    val text: String,
    val location: String,
    val lat: String,
    val lon: String,
    val userprofile: List<String>,
    val image: List<ImagePublication>,
    val iconprofile: List<IconUser>,
    val nicknamepublication: List<String>,
    val nicknamelike: List<String>,
    val countlike: List<Long>?,
    val iduserprofile: List<String>,
    val date: String,
    val idcounterlike: List<String>,
) : Parcelable

@Parcelize
data class IconUser(
    val url: String,
) : Parcelable

@Parcelize
data class ImagePublication(
    val url: String,
) : Parcelable


data class RecordUserProfileDTO(
    val id: String,
    val fields: FieldsUserProfileDTO,
)

data class FieldsUserProfileDTO(
    val nickname: String,
    val uid: String,
    val icon: List<IconUser>,
    val publication: List<String>,
    @SerializedName("likepublication")
    val likePublication: List<String>,
    val location: String,
    val datebirth: String,
    val usergender: Int,
)

data class Record(
    val id: String,
    val createdTime: String,
    val fields: Fields,
)

data class Fields(
    val id: Long,
    val location: String,
    val likeCount: Long,
    val text: String,
)