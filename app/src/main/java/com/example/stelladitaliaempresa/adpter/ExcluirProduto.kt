package com.example.stelladitaliaempresa.helper

import android.content.Context
import android.widget.Toast
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ExcluirProduto {

    private const val idEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // 🔥 Fixo

    fun excluir(context: Context, produto: ProdutoEntity) {
        val produtoId = produto.id ?: return

        FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idEmpresa)
            .child("produtos")
            .child(produtoId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Deleta local no Room usando coroutine
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getInstance(context).produtoDao().delete(produto)
                    }
                    Toast.makeText(context, "Produto excluído com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erro ao excluir produto", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
