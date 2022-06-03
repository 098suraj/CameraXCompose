package com.example.snigdhaapp.repo

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class authRepo(
    var context: Context,
    ) {
    lateinit var firebaseAuth:FirebaseAuth
    var database = Firebase.database
    private val _authState= MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(email: String?, password: String?, Username: String) {
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value= firebaseAuth.currentUser?.let { AuthState.Success(it) }!!
                    Toast.makeText(
                        context,
                        "Registered Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    database.reference
                        .child("Users").setValue(Username)
                } else {
                    Toast.makeText(
                        context,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    fun token() {}
    fun signIn(email: String?, password: String?) {
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value= firebaseAuth.currentUser?.let { AuthState.Success(it) }!!
                    Toast.makeText(
                        context,
                        "Login Successfully",
                        Toast.LENGTH_SHORT
                    ).show()


                } else {
                    Toast.makeText(
                      context,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    init {
        firebaseAuth=FirebaseAuth.getInstance()

    }




}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    class Success(val firebaseUser:FirebaseUser) : AuthState()
    class AuthError(val message: String? = null) : AuthState()
}