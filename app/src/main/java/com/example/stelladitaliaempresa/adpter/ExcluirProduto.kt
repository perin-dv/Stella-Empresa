package com.example.stelladitaliaempresa.helper

import android.content.Context
import android.widget.Toast
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.google.firebase.database.FirebaseDatabase

object ExcluirProduto {
    fun excluir(context: Context, produto: ProdutoEntity) {
        val empresaKey = UsuarioFirebase.getIdUsuario()
            .replace(".", "_dot_")
            .replace("@", "_at_")

        FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(empresaKey)
            .child("produtos")
            .child(produto.categoria ?: "sem_categoria")
            .child(produto.id ?: "")
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Thread { AppDatabase.getInstance(context).produtoDao().delete(produto) }.start()
                    Toast.makeText(context, "Produto excluído com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erro ao excluir produto", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
