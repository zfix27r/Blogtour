package com.myblogtour.airtable.domain

import com.google.gson.JsonObject
import retrofit2.Callback

interface RepoAirTable {

    fun createUserProfile(createUserProfile: JsonObject, callback: Callback<RecordUserProfileDTO>)
    fun createPostAirTable(createPost: JsonObject, callback: Callback<Record>)

    fun getPublication(callback: Callback<PublicationDTO>)
}