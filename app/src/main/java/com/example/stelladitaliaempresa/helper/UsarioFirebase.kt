package com.example.stelladitaliaempresa.helper

import com.google.firebase.auth.FirebaseUser

object UsuarioFirebase {

    fun getIdUsuario(): String =
        ConfiguracaoFirebase
            .getFirebaseAuth()
            .currentUser
            ?.uid
            ?: throw IllegalStateException("Usuário não está logado")

    /**
     * A chave que vamos usar em /empresa/{empresaKey}/...
     * Aqui, simplesmente o UID.
     */
    fun getEmpresaKey(): String = getIdUsuario()

    fun getUsuarioAtual() = ConfiguracaoFirebase.getFirebaseAuth().currentUser
    fun getEmailUsuario() = getUsuarioAtual()?.email.orEmpty()
    fun getFotoUsuario() = getUsuarioAtual()?.photoUrl?.toString().orEmpty()
}
