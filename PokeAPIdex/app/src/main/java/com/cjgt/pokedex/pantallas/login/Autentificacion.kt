package com.cjgt.pokedex.pantallas.login

import com.google.firebase.auth.FirebaseAuth

fun loginUser(email: String, password: String, onConfirm: () -> Unit, onError: () -> Unit) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onConfirm()
            } else onError()
        }
}

fun registerUser(
    email: String, password: String, onConfirm: () -> Unit, onError: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onConfirm()
            } else onError()
        }
}

