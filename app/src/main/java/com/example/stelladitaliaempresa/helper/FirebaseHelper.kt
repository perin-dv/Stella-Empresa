package com.example.stelladitaliaempresa.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseHelper {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun getIdUsuario(): String? {
        return auth.currentUser?.uid
    }

    fun isAutenticado(): Boolean {
        return auth.currentUser != null
    }
}
