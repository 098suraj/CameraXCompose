package com.example.snigdhaapp.ViewModel

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import com.example.snigdhaapp.repo.AuthState
import com.example.snigdhaapp.repo.authRepo
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    application: Application,

    ) : AndroidViewModel(application) {
    lateinit var authRepo: authRepo
    lateinit var authState: StateFlow<AuthState>

    init {
        authRepo = authRepo(application.applicationContext)
        authState =authRepo.authState

    }


    fun signUp(email: String?, password: String?, Username: String) {
        authRepo.signUp(email, password,Username)

    }

    fun singIn(email: String?, password: String?) {
        authRepo.signIn(email, password)
    }

    fun signOut() {
        authRepo.signOut()
    }


}
