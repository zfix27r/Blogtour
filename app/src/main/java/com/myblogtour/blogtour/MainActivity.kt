package com.myblogtour.blogtour

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myblogtour.blogtour.databinding.ActivityMainBinding
import com.myblogtour.blogtour.ui.authUser.AuthUserFragment
import com.myblogtour.blogtour.ui.home.HomeFragment
import com.myblogtour.blogtour.ui.profile.ProfileFragment
import com.myblogtour.blogtour.ui.registrationUser.RegistrationUserFragment

class MainActivity : AppCompatActivity() {

    private val auth by lazy { Firebase.auth }
    private var currentUser: FirebaseUser? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            toFragment(HomeFragment())
            binding.navMenuBottom.selectedItemId = R.id.btn_home_menu
        }

        initBottomMenu()
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }

    private fun toFragment(f: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, f).commit()
    }

    private fun initBottomMenu() {
        binding.navMenuBottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btn_favorites_menu -> {
                    true
                }
                R.id.btn_profile_menu -> {
                    openFragmentUserProfileAuth()
                    true
                }
                R.id.btn_home_menu -> {
                    toFragment(HomeFragment())
                    true
                }
                R.id.btn_more_menu -> {
                    true
                }
                R.id.btn_search_menu -> {
                    true
                }
                else ->
                    true

            }
        }
    }

    private fun openFragmentUserProfileAuth() {
        if (currentUser != null) {
            toFragment(ProfileFragment())
        } else {
            toFragment(RegistrationUserFragment())
        }
    }
}