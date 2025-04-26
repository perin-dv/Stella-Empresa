package com.example.stelladitaliaempresa.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.databinding.DialogEditarProdutoBinding
import com.google.firebase.database.FirebaseDatabase

object DialogEdicaoUtil {

    fun mostrarDialog(
        context: Context,
        produto: ProdutoEntity,
        onProdutoEditado: () -> Unit
    ) {
        val binding = DialogEditarProdutoBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.editNome.setText(produto.nome)
        binding.editObservacao.setText(produto.descricao)
        binding.editPreco.setText(produto.preco?.toString() ?: "")
        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString().trim()
            val descricao = binding.editObservacao.text.toString().trim()
            val preco = binding.editPreco.text.toString().toDoubleOrNull() ?: 0.0

            if (nome.isEmpty()) {
                binding.editNome.error = "Digite o nome"
                return@setOnClickListener
            }

            produto.nome = nome
            produto.descricao = descricao
            produto.preco = preco

            val idUsuario = produto.idUsuario ?: return@setOnClickListener
            val idProduto = produto.id ?: return@setOnClickListener

            val referencia = FirebaseDatabase.getInstance()
                .getReference("produtos")
                .child(idUsuario)
                .child(idProduto)

            referencia.setValue(produto)
                .addOnSuccessListener {
                    onProdutoEditado()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }

        dialog.show()
    }
}
