package com.myblogtour.blogtour.ui.profileUser

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import com.myblogtour.blogtour.domain.ImageUserProfileEntity
import com.myblogtour.blogtour.domain.UserProfileEntity

interface ProfileContract {

    interface ProfileViewModel {
        val userSuccess: LiveData<UserProfileEntity>
        val userError: LiveData<Throwable>
        val userSingOut: LiveData<Boolean>
        val verificationEmail: LiveData<String>
        val errorSaveProfile: LiveData<String>
        val successSaveUserProfile: LiveData<String>
        val errorUserLogin: LiveData<String>
        val errorLocationUser: LiveData<String>
        val progressLoadingImage: LiveData<ImageUserProfileEntity>
        val onSuccessLoadingImageUser: LiveData<Uri>


        fun onRefresh()
        fun singInOut()
        fun verificationEmail()
        fun saveReadUserProfile(
            loginUser: Editable?,
            locationUser: String,
            genderUser: Int,
            dateBirth: String,
        )

        fun changeImageProfileUser(uri: Uri)
    }
}