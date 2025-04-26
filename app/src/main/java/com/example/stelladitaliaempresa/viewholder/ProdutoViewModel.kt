package com.example.stelladitaliaempresa.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.imageutil.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProdutoViewModel(application: Application) : AndroidViewModel(application) {

    private val produtoDao = AppDatabase.getInstance(application).produtoDao()

    // ID fixo da empresa
    private val idUsuarioFirebase = "7a3118oNdgcpmwSqrgyRTqBnFFx2"

    /** Salvar novo produto no Firebase + Room */
    fun salvarProdutoFirebaseERoom(
        context: Context,
        nome: String,
        descricao: String,
        preco: Double,
        categoria: String?,
        uriImagem: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val imagemBase64 = uriImagem?.let {
                context.contentResolver.openInputStream(it)?.use { input ->
                    val bmp = BitmapFactory.decodeStream(input)
                    ImageUtils.bitmapToBase64(bmp)
                }
            }

            val referencia = FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(idUsuarioFirebase)
                .child("produtos")

            val chaveProduto = referencia.push().key
            if (chaveProduto.isNullOrEmpty()) {
                onError("Não foi possível gerar a chave do produto")
                return
            }

            val produto = ProdutoEntity(
                id = chaveProduto,
                nome = nome,
                descricao = descricao,
                preco = preco,
                imagem = imagemBase64,
                idUsuario = idUsuarioFirebase,
                categoria = categoria
            )

            referencia.child(chaveProduto)
                .setValue(produto)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        onError("Falha no Firebase: ${task.exception?.message}")
                        return@addOnCompleteListener
                    }

                    viewModelScope.launch(Dispatchers.IO) {
                        produtoDao.insert(produto)
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }

        } catch (e: Exception) {
            onError("Erro ao salvar produto: ${e.message}")
        }
    }

    /** Atualizar produto já existente no Firebase + Room */
    fun atualizarProdutoFirebaseERoom(
        produto: ProdutoEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val referencia = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuarioFirebase)
            .child("produtos")
            .child(produto.id ?: "")

        referencia.setValue(produto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModelScope.launch(Dispatchers.IO) {
                    produtoDao.update(produto)
                }
                onSuccess()
            } else {
                onError(task.exception?.message ?: "Erro ao atualizar no Firebase")
            }
        }
    }

    /** Atualizar um produto apenas no Room local */
    fun atualizarProdutoLocal(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoDao.update(produto)
        }
    }

    /** Salvar uma lista completa no Room local */
    fun salvarTodos(lista: List<ProdutoEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoDao.insertAll(lista)
        }
    }
}
