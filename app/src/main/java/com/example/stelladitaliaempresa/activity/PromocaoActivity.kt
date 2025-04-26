package com.example.stelladitaliaempresa.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.base.BaseActivity
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.ActivityPromocaoBinding
import com.example.stelladitaliaempresa.imageutil.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.animation.OvershootInterpolator
import java.util.UUID

class PromocaoActivity : BaseActivity() {

    private lateinit var binding: ActivityPromocaoBinding
    private var imagemBase64: String? = null
    private val produtosSelecionados = mutableListOf<ProdutoEntity>()
    private val dao by lazy { AppDatabase.getInstance(this).produtoDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromocaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtAnalise.alpha = 0f
        binding.txtAnalise.scaleX = 0.8f
        binding.txtAnalise.scaleY = 0.8f
        binding.txtAnalise.text = ""
        binding.txtAnalise.visibility = View.GONE

        binding.imgPromocao.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1001)
        }

        binding.editProdutos.setOnClickListener {
            selecionarProdutos()
        }

        binding.btnSalvarPromocao.setOnClickListener {
            confirmarSalvarPromocao()
        }

        binding.editValor.addTextChangedListener {
            atualizarResumoDesconto()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun selecionarProdutos() {
        lifecycleScope.launch {
            val todos = withContext(Dispatchers.IO) { dao.getTodos() }
            val nomes = todos.map { it.nome }.toTypedArray()
            val selecionadosTemp = BooleanArray(nomes.size) { i -> produtosSelecionados.contains(todos[i]) }

            AlertDialog.Builder(this@PromocaoActivity)
                .setTitle("Escolha até 4 produtos")
                .setMultiChoiceItems(nomes, selecionadosTemp) { dialog, which, isChecked ->
                    if (isChecked) {
                        if (produtosSelecionados.size >= 4) {
                            (dialog as AlertDialog).listView.setItemChecked(which, false)
                            Toast.makeText(this@PromocaoActivity, "Máximo de 4 produtos", Toast.LENGTH_SHORT).show()
                        } else {
                            produtosSelecionados.add(todos[which])
                        }
                    } else {
                        produtosSelecionados.remove(todos[which])
                    }
                }
                .setPositiveButton("Confirmar") { _, _ ->
                    binding.editProdutos.setText(produtosSelecionados.joinToString { it.nome ?: "" })
                    atualizarResumoDesconto()
                    animarAnalise("Selecione um valor para ver o desconto.")
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun atualizarResumoDesconto() {
        val precoPromocao = binding.editValor.text.toString().toDoubleOrNull()

        if (produtosSelecionados.isEmpty() || precoPromocao == null) {
            animarAnalise("Selecione produtos para ver o desconto.")
            return
        }

        val precoOriginal = produtosSelecionados.sumOf { it.preco ?: 0.0 }
        val desconto = precoOriginal - precoPromocao

        val resumo = """
        Você escolheu ${produtosSelecionados.size} produto(s).
        Valor original: R$ %.2f
        Promoção: R$ %.2f
        Desconto aplicado: R$ %.2f
        """.trimIndent().format(precoOriginal, precoPromocao, desconto)

        animarAnalise(resumo)
    }

    private fun animarAnalise(texto: String) {
        binding.txtAnalise.apply {
            text = texto
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    private fun confirmarSalvarPromocao() {
        AlertDialog.Builder(this)
            .setTitle("Salvar promoção")
            .setMessage("Tem certeza que deseja salvar esta promoção?")
            .setPositiveButton("Sim") { _, _ ->
                salvarPromocaoManual()
            }
            .setNegativeButton("Cancelar", null)
            .setCancelable(true)
            .show()
    }

    private fun salvarPromocaoManual() {
        val titulo = binding.editTituloPromocao.text.toString().trim()
        val observacao = binding.editObservacao.text.toString().trim()
        val preco = binding.editValor.text.toString().toDoubleOrNull()

        if (titulo.isEmpty() || observacao.isEmpty() || preco == null || imagemBase64 == null) {
            Toast.makeText(this, "Preencha todos os dados e selecione a imagem!", Toast.LENGTH_SHORT).show()
            return
        }

        val promocao = PromocaoEntity(
            id = System.currentTimeMillis().toString(),
            idUsuario = "7a3118oNdgcpmwSqrgyRTqBnFFx2",
            titulo = titulo,
            observacao = observacao,
            valor = preco,
            imagemBase64 = imagemBase64!!,
            produtos = produtosSelecionados.map { it.id ?: UUID.randomUUID().toString() }
        )

        salvarPromocao(promocao)
    }

    private fun salvarPromocao(promocao: PromocaoEntity) {
        val dao = AppDatabase.getInstance(this).promocaoDao()

        lifecycleScope.launch(Dispatchers.IO) {
            dao.insert(promocao)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@PromocaoActivity, "Promoção salva localmente", Toast.LENGTH_SHORT).show()
                finish() // ✅ Fecha aqui só depois de salvar certinho
            }
        }

        val firebaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child("7a3118oNdgcpmwSqrgyRTqBnFFx2")
            .child("promocoes")
            .child(promocao.id ?: System.currentTimeMillis().toString())

        firebaseRef.setValue(promocao).addOnSuccessListener {
            Log.d("SALVAR_PROMOCAO", "Promoção salva no Firebase com sucesso!")
        }.addOnFailureListener { e ->
            Log.e("SALVAR_PROMOCAO", "Erro ao salvar no Firebase: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val input = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                imagemBase64 = ImageUtils.bitmapToBase64(bitmap)
                binding.imgPromocao.setImageBitmap(bitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
