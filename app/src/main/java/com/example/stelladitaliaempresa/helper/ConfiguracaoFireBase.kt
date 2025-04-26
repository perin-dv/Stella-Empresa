package com.example.stelladitaliaempresa.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object ConfiguracaoFirebase {

    fun getDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    fun getFirebaseStorage(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()

    }
}
