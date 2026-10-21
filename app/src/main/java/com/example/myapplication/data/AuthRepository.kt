package com.example.myapplication.data//package com.example.myapplication.data

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth = FireBaseAuth.getInstance()
    private fun email(nim: String) = "${nim.trim()}@student.kampus.ac.id"

    fun isLoggedIn() = auth.currentUser != null
    fun currentNim(): String = auth.currentUser?.email?.substringBefore("@") ?: ""
    fun logout() = auth.signOut()

    /** callback: null = sukses, selain itu pesan error */
    fun login(nim: String, pass: String, cb: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email(nim), pass).addOnCompleteListener {
            cb(if (it.isSuccessful) null else it.exception?.localizedMessage ?: "Login gagal")
        }
    }
    fun register(nim: String, pass: String, cb: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email(nim), pass).addOnCompleteListener {
            cb(if (it.isSuccessful) null else it.exception?.localizedMessage ?: "Daftar gagal")
        }
    }
}