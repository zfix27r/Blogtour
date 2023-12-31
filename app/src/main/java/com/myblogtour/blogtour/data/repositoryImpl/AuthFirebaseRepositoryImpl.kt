package com.myblogtour.blogtour.data.repositoryImpl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.myblogtour.blogtour.domain.repository.AuthFirebaseRepository

class AuthFirebaseRepositoryImpl(private val userAuth: FirebaseAuth) : AuthFirebaseRepository {
    override fun userCurrent(onSuccess: (FirebaseUser) -> Unit, onError: (String) -> Unit) {
        userAuth.currentUser?.let {
            onSuccess.invoke(it)
            return
        }
        onError.invoke("Необходимо авторизоваться")
    }

    override fun isEmailVerified(onSuccess: (Boolean) -> Unit) {
        userAuth.currentUser?.let {
           onSuccess.invoke(it.isEmailVerified)
        }
    }

    override fun authUser(
        emailAuth: String,
        passwordAuth: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        userAuth.signInWithEmailAndPassword(
            emailAuth, passwordAuth
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess.invoke(true)
            } else {
                onError.invoke("Что-то пошло не так")
            }
        }
    }

    override fun registrationUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess.invoke(userAuth.uid!!)
            } else {
                onError.invoke("Попробуйте позже")
            }
        }
    }

    override fun updateUser(idAirtable: String, icon: String, onSuccess: (Boolean) -> Unit) {
        val updateProfile = userProfileChangeRequest {
            displayName = idAirtable
            photoUri = Uri.parse(icon)
        }
        userAuth.currentUser?.let {
            it.updateProfile(updateProfile)
            onSuccess.invoke(true)
        }
    }

    override fun resetPassword(
        emailAccount: String,
        onSuccess: (Boolean) -> Unit,
    ) {
        userAuth.sendPasswordResetEmail(emailAccount).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess.invoke(true)
            } else {
                onSuccess.invoke(false)
            }
        }
    }

    override fun singInOut(onSuccess: (Boolean) -> Unit) {
        userAuth.signOut()
    }

    override fun verificationEmail(onSuccess: (Boolean) -> Unit) {
        userAuth.currentUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(true)
                } else {
                    onSuccess.invoke(false)
                }
            }
        }
    }

    override fun deleteAccountUser() {
        userAuth.currentUser?.let {
            it.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val itDelete = task.result
                }
            }
        }
    }
}