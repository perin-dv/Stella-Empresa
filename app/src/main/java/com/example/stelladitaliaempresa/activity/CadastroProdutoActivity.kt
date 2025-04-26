package com.example.stelladitaliaempresa.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.base.BaseActivity
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.ActivityNovoProdutoActivitiyBinding
import com.example.stelladitaliaempresa.helper.UsuarioFirebase
import com.google.firebase.database.FirebaseDatabase
import com.example.stelladitaliaempresa.data.AppDatabase as DataBase
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroProdutoActivity : BaseActivity() {

    private lateinit var binding: ActivityNovoProdutoActivitiyBinding
    private var imagemUri: Uri? = null
    private var imagemBase64: String? = null

    private val selecionarImagem =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemUri = result.data?.data
                imagemUri?.let { uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.productImagePreview.setImageBitmap(bitmap)

                    // Converte para base64
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val imagemBytes = outputStream.toByteArray()
                    imagemBase64 = Base64.encodeToString(imagemBytes, Base64.DEFAULT)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoProdutoActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            selecionarImagem.launch(intent)
        }

        binding.buttoncadastro.setOnClickListener {
            validarDados()
        }

    }

    private fun validarDados() {
        val nome = binding.editProductName.text.toString().trim()
        val descricao = binding.editProductDescription.text.toString().trim()
        val preco = binding.editProductPrice.text.toString().toDoubleOrNull()

        if (nome.isEmpty() || descricao.isEmpty() || preco == null || imagemBase64 == null) {
            Toast.makeText(
                this,
                "Preencha todos os campos e selecione uma imagem!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        salvarProduto(nome, descricao, preco, imagemBase64!!)
    }

    private fun salvarProduto(
        nome: String,
        descricao: String,
        preco: Double,
        imagemBase64: String
    ) {
        val idUsuarioOriginal = UsuarioFirebase.getIdUsuario()
        val idUsuarioFirebase = idUsuarioOriginal
            .replace(".", "_dot_")
            .replace("@", "_at_")

        val idProduto = System.currentTimeMillis().toString()

        val produto = ProdutoEntity(
            id = idProduto,
            nome = nome,
            descricao = descricao,
            preco = preco,
            imagem = imagemBase64,
            idUsuario = idUsuarioOriginal
        )

        val produtosRef = FirebaseDatabase.getInstance()
            .getReference("empresa") // 💡 Agora usa o mesmo nó de empresa que promoções
            .child(idUsuarioFirebase)
            .child("produtos")
            .child(idProduto)

        produtosRef.setValue(produto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show()
                salvarLocal(produto)
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar produto!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun salvarLocal(produto: ProdutoEntity) {
        lifecycleScope.launch {
            val dao = DataBase.getInstance(applicationContext).produtoDao()

            val entity = ProdutoEntity(
                idLocal = 0,
                id = produto.id,
                nome = produto.nome,
                descricao = produto.descricao,
                preco = produto.preco,
                imagem = produto.imagem,
                idUsuario = produto.idUsuario
            )

            withContext(Dispatchers.IO) {
                dao.insert(entity)
            }
        }
    }
    private fun salvarPromocao(promocao: PromocaoEntity) {
        val dao = AppDatabase.getInstance(this).promocaoDao()

        // Salva no Room (corroutine-safe)
        lifecycleScope.launch(Dispatchers.IO) {
            dao.insert(promocao)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@CadastroProdutoActivity, "Promoção salva localmente", Toast.LENGTH_SHORT).show()
            }
        }

        // Salva no Firebase
        val idUsuarioOriginal = UsuarioFirebase.getIdUsuario()
        val idUsuarioFirebase = idUsuarioOriginal
            .replace(".", "_dot_")
            .replace("@", "_at_")

        val idPromocao = System.currentTimeMillis().toString()

        val promocaoFirebase = PromocaoEntity(
            id = idPromocao,
            idUsuario = idUsuarioOriginal,
            titulo = promocao.titulo,
            observacao = promocao.observacao,
            valor = promocao.valor,
            imagemBase64 = promocao.imagemBase64,
            produtos = promocao.produtos
        )

        val firebaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuarioFirebase)
            .child("promocoes")
            .child(idPromocao)

        firebaseRef.setValue(promocaoFirebase).addOnSuccessListener {
            Log.d("SALVAR_PROMOCAO", "Promoção salva no Firebase com sucesso!")
        }.addOnFailureListener { e ->
            Log.e("SALVAR_PROMOCAO", "Erro ao salvar no Firebase: ${e.message}")
        }
    }

}
