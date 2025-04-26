package com.example.stelladitaliaempresa.util

import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.model.ItemCardapio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FirebaseUtil {

    fun buscarProdutos(onResult: (List<ItemCardapio>) -> Unit) {
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("produtos")
            .child(idUsuario)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaFinal = mutableListOf<ItemCardapio>()

                val produtosPorCategoria = mutableMapOf<String, MutableList<ProdutoEntity>>()

                for (produtoSnapshot in snapshot.children) {
                    val produto = produtoSnapshot.getValue(ProdutoEntity::class.java)
                    produto?.let {
                        val categoria = it.categoria ?: "Outros"
                        produtosPorCategoria.getOrPut(categoria) { mutableListOf() }.add(it)
                    }
                }

                // Organiza em lista com títulos e itens
                produtosPorCategoria.forEach { (categoria, produtos) ->
                    listaFinal.add(ItemCardapio.TituloCategoria(categoria))
                    produtos.forEach {
                        listaFinal.add(ItemCardapio.ProdutoItem(it))
                    }
                }

                onResult(listaFinal)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }
}
