package com.example.stelladitaliaempresa.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.example.stelladitaliaempresa.base.BaseActivity
import com.example.stelladitaliaempresa.databinding.ActivityNovoProdutoActivitiyBinding
import com.example.stelladitaliaempresa.viewmodel.ProdutoViewModel

class NovoProdutoActivity : BaseActivity() {

    private lateinit var binding: ActivityNovoProdutoActivitiyBinding
    private val produtoViewModel: ProdutoViewModel by viewModels()
    private var imagemUriSelecionada: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoProdutoActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar com botão voltar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Categorias no spinner
        val categorias = listOf(
            "Pizza Tradicional", "Pizza Especial", "Pizza Premium",
            "Pizza Vegetariana", "Pizza Doce", "Porções",
            "Combos", "Bebidas", "Bebidas sem álcool"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spinnerCategoria.setAdapter(adapter)
        binding.spinnerCategoria.setOnClickListener { binding.spinnerCategoria.showDropDown() }

        // Seleção de imagem
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1001)
        }

        // Botão de salvar
        binding.buttoncadastro.setOnClickListener {
            salvarProduto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            imagemUriSelecionada = data.data
            binding.productImagePreview.setImageURI(imagemUriSelecionada)
        }
    }

    private fun salvarProduto() {
        val nome = binding.editProductName.text.toString().trim()
        val descricao = binding.editProductDescription.text.toString().trim()
        val precoStr = binding.editProductPrice.text.toString().trim()
        val categoria = binding.spinnerCategoria.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || precoStr.isEmpty() || imagemUriSelecionada == null || categoria.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val preco = precoStr.toDoubleOrNull()
        if (preco == null) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show()
            return
        }

        produtoViewModel.salvarProdutoFirebaseERoom(
            context     = this,
            nome        = nome,
            descricao   = descricao,
            preco       = preco,
            categoria   = categoria,
            uriImagem   = imagemUriSelecionada!!,
            onSuccess   = {
                Toast.makeText(this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError     = { erro ->
                Toast.makeText(this, "Erro ao salvar produto: $erro", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
