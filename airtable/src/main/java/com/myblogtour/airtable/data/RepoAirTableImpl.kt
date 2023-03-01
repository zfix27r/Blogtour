package com.myblogtour.airtable.data

import com.google.gson.JsonObject
import com.myblogtour.airtable.BuildConfig
import com.myblogtour.airtable.domain.*
import com.myblogtour.airtable.domain.retrofit.RetrofitAirTable
import retrofit2.Callback

class RepoAirTableImpl : RepoAirTable {

    private val retrofitAirTable by lazy { RetrofitAirTable.startRetrofit() }

    override fun createUserProfile(
        createUserProfile: JsonObject,
        callback: Callback<RecordUserProfileDTO>,
    ) {
        retrofitAirTable.createUserProfile(BuildConfig.API_KEY, createUserProfile).enqueue(callback)
    }

    override fun createPublication(createPost: JsonObject, callback: Callback<Record>) {
        retrofitAirTable.createPublication(BuildConfig.API_KEY, createPost).enqueue(callback)
    }

    override fun updateComplaintPublication(
        id: String,
        complaint: JsonObject,
        callback: Callback<Unit>
    ) {
        retrofitAirTable.updatePublicationComplaint(id, BuildConfig.API_KEY, complaint)
            .enqueue(callback)
    }

    override fun getPublication(callback: Callback<PublicationDTO>) {
        retrofitAirTable.getListPublication(BuildConfig.API_KEY).enqueue(callback)
    }

    override fun getMyPublication(id: String, callback: Callback<PublicationDTO>) {
        retrofitAirTable.getMyPublication(BuildConfig.API_KEY, id).enqueue(callback)
    }

    override fun getUserProfile(id: String, callback: Callback<UserProfileDTO>) {
        retrofitAirTable.getProfileUser(BuildConfig.API_KEY, id)
            .enqueue(callback)
    }

    override fun updateUserProfileLikeCounter(
        id: String,
        update: JsonObject,
        callback: Callback<Unit>,
    ) {
        retrofitAirTable.updateUserProfileCounterLike(id, BuildConfig.API_KEY, update)
            .enqueue(callback)
    }

    override fun deletePublication(idPublication: String, callback: Callback<Unit>) {
        retrofitAirTable.deletePublication(idPublication, BuildConfig.API_KEY).enqueue(callback)
    }

    override fun deletePublicationLike(idLike: String, callback: Callback<Unit>) {
        retrofitAirTable.deletePublicationLike(idLike,BuildConfig.API_KEY).enqueue(callback)
    }
}