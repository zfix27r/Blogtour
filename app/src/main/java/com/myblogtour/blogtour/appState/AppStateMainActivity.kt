package com.myblogtour.blogtour.appState

sealed class AppStateMainActivity {
    data class CurrentUser(val currentUser: Boolean) : AppStateMainActivity()
}