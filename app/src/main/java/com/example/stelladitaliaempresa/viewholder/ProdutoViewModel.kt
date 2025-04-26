package com.example.stelladitaliaempresa.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.helper.UsuarioFirebase
import com.example.stelladitaliaempresa.imageutil.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ProdutoViewModel(application: Application) : AndroidViewModel(application) {

    private val produtoDao = AppDatabase.getInstance(application).produtoDao()


    // app/src/main/java/com/example/stelladitaliaempresa/viewmodel/ProdutoViewModel.kt
    class ProdutoViewModel(application: Application) : AndroidViewModel(application) {
        private val produtoDao = AppDatabase.getInstance(application).produtoDao()

        /** Atualiza no Firebase e, em caso de sucesso, no Room */
        fun atualizarProdutoFirebaseERoom(
            produto: ProdutoEntity,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val usuarioId = UsuarioFirebase.getIdUsuario()
            val empresaKey = usuarioId.replace("@", "_at_").replace(".", "_dot_")
            val cat = produto.categoria ?: "sem_categoria"

            val ref = FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(empresaKey)
                .child("produtos")
                .child(cat)
                .child(produto.id ?: "")
            ref.setValue(produto).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Atualiza localmente
                    viewModelScope.launch(Dispatchers.IO) {
                        produtoDao.update(produto)
                    }
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Erro ao atualizar no Firebase")
                }
            }
        }
    }

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
        val usuarioId = UsuarioFirebase.getIdUsuario()
        val empresaKey = usuarioId
            .replace(".", "_dot_")
            .replace("@", "_at_")

        try {
            // converte a imagem para Base64
            val imagemBase64 = uriImagem?.let {
                context.contentResolver.openInputStream(it)?.use { input ->
                    val bmp = BitmapFactory.decodeStream(input)
                    ImageUtils.bitmapToBase64(bmp)
                }
            }

            // gera a referência Firebase
            val referencia = FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(empresaKey)
                .child("produtos")
                .child(categoria ?: "sem_categoria")

            val chaveProduto = referencia.push().key
            if (chaveProduto.isNullOrEmpty()) {
                onError("Não foi possível gerar a chave do produto")
                return
            }

            // monta a entidade
            val produto = ProdutoEntity(
                id        = chaveProduto,
                nome      = nome,
                descricao = descricao,
                preco     = preco,
                imagem    = imagemBase64,
                idUsuario = usuarioId,
                categoria = categoria
            )

            // grava no Firebase
            referencia.child(chaveProduto)
                .setValue(produto)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        onError("Falha no Firebase: ${task.exception?.message}")
                        return@addOnCompleteListener
                    }

                    // só depois de ter certeza do Firebase, grava no Room e só então notifica onSuccess
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

    /** Atualiza um produto já existente no Room (chamado pelo seu diálogo de edição) */
    fun atualizarProdutoLocal(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoDao.update(produto)
        }
    }

    /** Insere uma lista completa no Room (útil após um sync) */
    fun salvarTodos(lista: List<ProdutoEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoDao.insertAll(lista)
        }
    }
}
