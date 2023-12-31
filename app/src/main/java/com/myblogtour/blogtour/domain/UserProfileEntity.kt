package com.myblogtour.blogtour.domain

data class UserProfileEntity(
    val id: String,
    val uid: String,
    val nickname: String,
    val publication: List<String>?,
    val icon: String,
    val likePublication: List<String>?,
    val verification: Boolean,
    val email: String,
    val location: String,
    val datebirth: String,
    val usergender: Int
)
