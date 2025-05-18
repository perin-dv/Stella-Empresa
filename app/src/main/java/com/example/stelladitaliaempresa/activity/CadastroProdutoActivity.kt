package com.example.stelladitaliaempresa.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.base.BaseActivity
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.ActivityNovoProdutoActivitiyBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

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

                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val imagemBytes = outputStream.toByteArray()
                    imagemBase64 = Base64.encodeToString(imagemBytes, Base64.DEFAULT)
                }
            }
        }

    private val idUsuarioFirebase = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // UID fixo

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
            Toast.makeText(this, "Preencha todos os campos e selecione uma imagem!", Toast.LENGTH_SHORT).show()
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
        val idProduto = System.currentTimeMillis().toString()
        val categoria = binding.spinnerCategoria.text.toString().trim()

        val produto = ProdutoEntity(
            id = idProduto,
            nome = nome,
            descricao = descricao,
            preco = preco,
            imagem = imagemBase64,
            categoria = categoria,
            idUsuario = idUsuarioFirebase
        )

        // Salvar por categoria (estrutura otimizada)
        val refPorCategoria = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuarioFirebase)
            .child("categorias")
            .child(categoria)
            .child(idProduto)

        refPorCategoria.setValue(produto)

        // Salvar também no caminho antigo (opcional)
        val refGlobal = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuarioFirebase)
            .child("produtos")
            .child(idProduto)

        refGlobal.setValue(produto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show()
                salvarLocal(produto)
                perguntarSalvarPromocao(produto)
            } else {
                Toast.makeText(this, "Erro ao salvar produto!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun salvarLocal(produto: ProdutoEntity) {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).produtoDao()
            withContext(Dispatchers.IO) {
                dao.insert(produto)
            }
        }
    }

    private fun perguntarSalvarPromocao(produto: ProdutoEntity) {
        AlertDialog.Builder(this)
            .setTitle("Salvar como promoção?")
            .setMessage("Deseja também salvar este produto como uma promoção?")
            .setPositiveButton("Sim") { _, _ -> perguntarDesconto(produto) }
            .setNegativeButton("Não") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun perguntarDesconto(produto: ProdutoEntity) {
        val input = EditText(this)
        input.hint = "Digite o valor com desconto"

        AlertDialog.Builder(this)
            .setTitle("Definir preço promocional")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val precoPromocional = input.text.toString().toDoubleOrNull() ?: produto.preco
                perguntarDestaque(produto, precoPromocional)
            }
            .setNegativeButton("Cancelar") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun perguntarDestaque(produto: ProdutoEntity, precoPromocional: Double) {
        AlertDialog.Builder(this)
            .setTitle("Destaque da semana?")
            .setMessage("Quer marcar esta promoção como destaque?")
            .setPositiveButton("Sim") { _, _ -> salvarPromocao(produto, precoPromocional, true) }
            .setNegativeButton("Não") { _, _ -> salvarPromocao(produto, precoPromocional, false) }
            .setCancelable(false)
            .show()
    }

    private fun salvarPromocao(produto: ProdutoEntity, precoPromocional: Double, destaque: Boolean) {
        val idPromocao = System.currentTimeMillis().toString()

        val promocaoFirebase = PromocaoEntity(
            id = idPromocao,
            idUsuario = idUsuarioFirebase,
            titulo = if (destaque) "🌟 ${produto.nome}" else produto.nome ?: "Promoção",
            observacao = produto.descricao ?: "",
            valor = precoPromocional,
            imagemBase64 = produto.imagem,
            produtos = emptyList()

        )

        val firebaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuarioFirebase)
            .child("promocoes")
            .child(idPromocao)

        firebaseRef.setValue(promocaoFirebase).addOnSuccessListener {
            Toast.makeText(this, "Promoção salva com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao salvar promoção", Toast.LENGTH_SHORT).show()
        }
    }
}