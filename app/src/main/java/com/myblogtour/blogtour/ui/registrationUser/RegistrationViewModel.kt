package com.myblogtour.blogtour.ui.registrationUser

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.myblogtour.blogtour.appState.AppStateUserRegistration
import com.myblogtour.blogtour.domain.repository.AuthFirebaseRepository
import com.myblogtour.blogtour.domain.repository.UserRegistrationRepository
import com.myblogtour.blogtour.utils.converterFromRegisterUserAirtableToUserEntity
import com.myblogtour.blogtour.utils.validatorEmail.EmailValidatorPatternImpl
import com.myblogtour.blogtour.utils.validatorPassword.PasswordValidatorPatternImpl
import com.myblogtour.blogtour.utils.validatorUserName.LoginValidatorPatternImpl

class RegistrationViewModel(
    private val authFirebaseRepository: AuthFirebaseRepository,
    private val userRegistrationRepository: UserRegistrationRepository,
    private val storageRef: StorageReference,
    private val liveData: MutableLiveData<AppStateUserRegistration> = MutableLiveData(),
    private val validEmailPattern: EmailValidatorPatternImpl = EmailValidatorPatternImpl(),
    private val validPasswordPattern: PasswordValidatorPatternImpl = PasswordValidatorPatternImpl(),
    private val validNameValidatorPattern: LoginValidatorPatternImpl = LoginValidatorPatternImpl(),
) : ViewModel() {

    private lateinit var userLogin: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var uriIconUser: Uri

    private var nameFile: StorageReference? = null
    private var uploadTask: UploadTask? = null

    fun getLiveData() = liveData

    fun singOut(){
        authFirebaseRepository.singInOut {

        }
    }

    fun registerUserFb(
        loginUserRegister: Editable?,
        emailRegister: Editable?,
        passwordOneRegister: Editable?,
        passwordTwoRegister: Editable?,
    ) {
        validNameValidatorPattern.afterTextUserName(loginUserRegister)
        when {
            validNameValidatorPattern.validUserLogin -> {
                userLogin = loginUserRegister.toString()
                emailValidator(emailRegister, passwordOneRegister, passwordTwoRegister)
            }
            validNameValidatorPattern.nullUserLogin == null -> {
                liveData.postValue(AppStateUserRegistration.ErrorUserLogin("Введите логин"))
            }
            else -> {
                liveData.postValue(AppStateUserRegistration.ErrorUserLogin("Некорректный логин"))
            }
        }
    }

    // Регистрируем аккаунт в google
    private fun createAccount() {
        authFirebaseRepository.registrationUser(email, password,
            onSuccess = {
                createProfileUserAirtable(it)
            },
            onError = {
                liveData.postValue(AppStateUserRegistration.ErrorUser(it))
            })
    }

    private fun setDisplayNameIdAirtable(idAirtable: String, icon: String) {
        authFirebaseRepository.updateUser(
            idAirtable,
            icon,
            onSuccess = {
                liveData.postValue(AppStateUserRegistration.SuccessUser(it))
            })
    }

    private fun passwordValidator(
        passwordOneRegister: Editable?,
        passwordTwoRegister: Editable?,
    ) {
        validPasswordPattern.afterText(passwordOneRegister, passwordTwoRegister)
        when {
            validPasswordPattern.validPassword -> {
                if (validPasswordPattern.equalsPassword) {
                    password = passwordTwoRegister.toString()
                    createAccount()
                } else {
                    liveData.postValue(AppStateUserRegistration.ErrorPasswordEquals("Пароли не совпадают"))
                }
            }
            else -> {
                liveData.postValue(AppStateUserRegistration.ErrorPassword("Легкий пароль"))
            }
        }
    }

    private fun emailValidator(
        emailRegister: Editable?,
        passwordOneRegister: Editable?,
        passwordTwoRegister: Editable?,
    ) {
        validEmailPattern.afterText(emailRegister)
        when {
            validEmailPattern.validEmail -> {
                email = emailRegister.toString()
                passwordValidator(passwordOneRegister, passwordTwoRegister)
            }
            validEmailPattern.textEmail == null -> {
                liveData.postValue(AppStateUserRegistration.ErrorEmail("Введите email"))
            }
            else -> {
                liveData.postValue(AppStateUserRegistration.ErrorEmail("Email введен некорректно"))
            }
        }
    }

    fun loadingIconUserProfile(uri: Uri) {
        nameFile = storageRef.child("icon/${uri.lastPathSegment}")
        uploadTask = nameFile?.let {
            it.putFile(uri)
        }
        uploadTask?.let { uploadTask ->
            with(uploadTask) {
                addOnFailureListener {

                }
                addOnSuccessListener {
                    liveData.postValue(AppStateUserRegistration.SuccessIconUser(true))
                    getDownloadUrl()
                }
                addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    liveData.postValue(AppStateUserRegistration.ProgressLoadingIconUser(progress.toInt()))
                }
            }
        }
    }

    private fun getDownloadUrl() {
        uploadTask?.let { upload ->
            upload.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { ex ->
                        throw ex
                    }
                }
                nameFile?.let {
                    it.downloadUrl
                }
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uriIconUser = task.result
                }
            }
        }
    }

    //Регистрируем профиль в Airtable
    private fun createProfileUserAirtable(uid: String) {
        val fieldsJsonObject = JsonObject()
        val userIconJsonArray = JsonArray()
        val userJsonObject = JsonObject()
        val urlIconJsonObject = JsonObject()

        urlIconJsonObject.addProperty("url", uriIconUser.toString())
        userIconJsonArray.add(urlIconJsonObject)
        userJsonObject.addProperty("uid", uid)
        userJsonObject.addProperty("nickname", userLogin)
        userJsonObject.add("icon", userIconJsonArray)
        fieldsJsonObject.add("fields", userJsonObject)

        userRegistrationRepository.createUser(
            fieldsJsonObject,
            onSuccess = {
                val userProfile = converterFromRegisterUserAirtableToUserEntity(it)
                setDisplayNameIdAirtable(userProfile.id, userProfile.icon)
            },
            onError = {
                authFirebaseRepository.deleteAccountUser()
                liveData.postValue(AppStateUserRegistration.ErrorUser("Что-то пошло не так"))
            }
        )
    }

    fun deleteImage() {
        nameFile?.let {
            it.delete().addOnSuccessListener {
                liveData.postValue(AppStateUserRegistration.DeleteIconUser(true))
            }.addOnFailureListener {

            }
        }
    }

    fun deleteImageDetach() {
        nameFile?.let {
            it.delete()
        }
    }
}